/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.book;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */

@RestController
@RequestMapping("/v1/viewBooksForSelling/userLoggedIn/{userLoggedIn}")
public class ViewBooksForSelling {
    
    private static final Logger logger = LoggerFactory.getLogger(ViewBooksForSelling.class);
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;

    @GetMapping
    @ApiOperation(value = "Returns list of available books", notes = "Returns list of available books")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Returns list of available books."),
            @ApiResponse(code = 204, message = "No books available."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to return available books
    public ResponseEntity viewBooksForSelling(@PathVariable(value = "userLoggedIn") String userLoggedIn)
            throws IOException {
        
        statsd.incrementCounter("viewBooksForSellingApi");
        long start = System.currentTimeMillis();
        
        User user = userRepository.findByUserEmailAddress(userLoggedIn);

        ArrayList<Book> availableBooks = bookRepository.findAll();
        long dbEnd = System.currentTimeMillis();
        long dbTimeElapsed = dbEnd - start;
        statsd.recordExecutionTime("viewBooksForSellingDBTime", dbTimeElapsed);

        ArrayList<Book> returnAvailableBooks = new ArrayList<Book>();
        if (availableBooks != null && availableBooks.size() > 0) {
            for (Book book : availableBooks) {
                if (book.getBookSoldBy().equals(userLoggedIn)) {
                    returnAvailableBooks.add(book);
                }
            }
        }
        
        if (returnAvailableBooks != null && returnAvailableBooks.size() > 0) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("viewBooksForSellingApiTime", timeElapsed);
            logger.info("**********Returned list of books put up for selling!**********");
            HttpHeaders headers = new HttpHeaders();
            headers.add("session-id", user.getUuid());
            return ResponseEntity.ok().headers(headers).body(returnAvailableBooks);
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("viewBooksForSellingApiTime", timeElapsed);
            logger.info("**********No books put up for selling !**********");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}


