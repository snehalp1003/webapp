/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.book;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.utility.UtilityService;

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

        if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy) != null) {

            return new ResponseEntity("Duplicate entry !", HttpStatus.CONFLICT);
        } else if (UtilityService.checkStringNotNull(bookISBN) && UtilityService.checkStringNotNull(bookSoldBy)) {
            if (UtilityService.checkIfValidBookPrice(bookDetails.getBookPrice())
                    && UtilityService.checkIfValidBookQuantity(bookDetails.getBookQuantity())) {
                bookDetails.setBookAdded(new Date());
                bookDetails.setBookLastModified(new Date());
                bookRepository.save(bookDetails);

                return new ResponseEntity(bookDetails, HttpStatus.OK);
            } else {
                return new ResponseEntity("Invalid book quantity or book price",HttpStatus.NOT_ACCEPTABLE);
            }
            
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
