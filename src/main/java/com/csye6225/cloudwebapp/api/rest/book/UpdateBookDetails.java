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
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/v1/updateBookDetails/bookISBN/{bookISBN}/bookSoldBy/{bookSoldBy}/userLoggedIn/{userLoggedIn}")
public class UpdateBookDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateBookDetails.class);

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;
    
    @PutMapping
    @ApiOperation(value = "Updates book details", notes = "Updates new book details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book details inserted successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 406, message = "Invalid book quantity or book price."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to update book details
    public ResponseEntity updateBookDetails(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "userLoggedIn") String userLoggedIn,
            @RequestBody Book bookDetails) throws IOException {
        
        User user = userRepository.findByUserEmailAddress(userLoggedIn);
        if (user == null || !UtilityService.checkStringNotNull(user.getUuid())) {
            logger.info("**********Session expired for user**********");
            return new ResponseEntity("Session expired for user.", HttpStatus.REQUEST_TIMEOUT);
        }

        if(!userLoggedIn.equals(bookDetails.getBookSoldBy())) {
            return new ResponseEntity("Cannot update book belonging to another seller !", HttpStatus.FORBIDDEN);
        } else if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookDetails.getBookSoldBy()) != null) {
            Book book = bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookDetails.getBookSoldBy());
            if (UtilityService.checkIfValidBookPrice(bookDetails.getBookPrice())
                    && UtilityService.checkIfValidBookQuantity(bookDetails.getBookQuantity())) {
                book.setBookISBN(book.getBookISBN());
                book.setBookTitle(bookDetails.getBookTitle());
                book.setBookAuthors(bookDetails.getBookAuthors());
                book.setBookPubDate(bookDetails.getBookPubDate());
                book.setBookQuantity(bookDetails.getBookQuantity());
                book.setBookPrice(bookDetails.getBookPrice());
                book.setBookSoldBy(book.getBookSoldBy());
                book.setBookAdded(book.getBookAdded());
                book.setBookLastModified(new Date());
                bookRepository.save(book);
                
                return new ResponseEntity(book, HttpStatus.OK);
            } else {
                return new ResponseEntity("Invalid book quantity or book price",HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity("Book not found !", HttpStatus.NOT_FOUND);
        }

    }
}
