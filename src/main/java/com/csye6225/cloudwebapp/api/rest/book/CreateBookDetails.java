/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.book;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.UserRepository;
import com.csye6225.cloudwebapp.utility.UtilityService;
import com.timgroup.statsd.StatsDClient;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/insertBookDetails/bookISBN/{bookISBN}/bookSoldBy/{bookSoldBy}")
public class CreateBookDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateBookDetails.class);

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;

    @PostMapping
    @ApiOperation(value = "Creates new book details", notes = "Creates new book details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book details inserted successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 406, message = "Invalid book quantity or book price."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to insert book details
    public ResponseEntity createBookDetails(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "bookSoldBy") String bookSoldBy, @RequestBody Book bookDetails) throws IOException {
        
        statsd.incrementCounter("createBookDetailsApi");
        long start = System.currentTimeMillis();
        User user = userRepository.findByUserEmailAddress(bookSoldBy);
        if (user == null || !UtilityService.checkStringNotNull(user.getUuid())) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("createBookDetailsApiTime", timeElapsed);
            logger.info("**********Session expired for user**********");
            return new ResponseEntity("Session expired for user.", HttpStatus.REQUEST_TIMEOUT);
        }

        if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy) != null) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("createBookDetailsApiTime", timeElapsed);
            logger.info("**********Book already exists in database !**********");

            return new ResponseEntity("Duplicate entry !", HttpStatus.CONFLICT);
        } else if (UtilityService.checkStringNotNull(bookISBN) && UtilityService.checkStringNotNull(bookSoldBy)) {
            if (UtilityService.checkIfValidBookPrice(bookDetails.getBookPrice())
                    && UtilityService.checkIfValidBookQuantity(bookDetails.getBookQuantity())) {
                bookDetails.setBookAdded(new Date());
                bookDetails.setBookLastModified(new Date());
                long dbStart = System.currentTimeMillis();
                bookRepository.save(bookDetails);
                
                long end = System.currentTimeMillis();
                long dbTimeElapsed = end - dbStart;
                long timeElapsed = end - start;
                statsd.recordExecutionTime("saveBookToDBTime", dbTimeElapsed);
                statsd.recordExecutionTime("createBookDetailsApiTime", timeElapsed);
                logger.info("**********Inserting New Book**********");
                HttpHeaders headers = new HttpHeaders();
                headers.add("session-id", user.getUuid());
                return ResponseEntity.ok().headers(headers).body(bookDetails);
            } else {
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                statsd.recordExecutionTime("createBookDetailsApiTime", timeElapsed);
                logger.info("**********Invalid book quantity or book price**********");
                return new ResponseEntity("Invalid book quantity or book price",HttpStatus.NOT_ACCEPTABLE);
            }
            
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("createBookDetailsApiTime", timeElapsed);
            logger.info("**********Other Error**********");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
