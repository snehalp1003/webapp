/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.cart;

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
import com.csye6225.cloudwebapp.api.model.Cart;
import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.CartRepository;
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
@RequestMapping("/v1/addBookToCart/bookBoughtBy/{bookBoughtBy}/bookISBN/{bookISBN}")
public class AddBookToCart {
    
    private static final Logger logger = LoggerFactory.getLogger(AddBookToCart.class);
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;
    
    @PostMapping
    @ApiOperation(value = "Adds book to cart", notes = "Adds book to cart")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book added successfully to cart"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 406, message = "Requested quantity not available."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to add book to cart
    public ResponseEntity addBookToCart(@PathVariable(value = "bookBoughtBy") String bookBoughtBy,
            @PathVariable(value = "bookISBN") String bookISBN,
            @RequestBody Cart cartItem) throws IOException {
        
        statsd.incrementCounter("addBookToCartApi");
        long start = System.currentTimeMillis();
        
        User user = userRepository.findByUserEmailAddress(bookBoughtBy);
        if (user == null || !UtilityService.checkStringNotNull(user.getUuid())) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("addBookToCartApiTime", timeElapsed);
            logger.info("**********Session expired for user**********");
            return new ResponseEntity("Session expired for user.", HttpStatus.REQUEST_TIMEOUT);
        }

        if (cartRepository.findByBookBoughtByAndBookISBN(bookBoughtBy, bookISBN) != null) {
            
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else if (UtilityService.checkStringNotNull(bookBoughtBy)
                && UtilityService.checkStringNotNull(bookISBN)) {
            
            String bookSoldBy = cartItem.getBookSoldBy();
            Book book = bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy);
            if (book == null) {
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                statsd.recordExecutionTime("addBookToCartApiTime", timeElapsed);
                logger.info("**********Book not found**********");
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            if(book.getBookQuantity() < 1) {
                return new ResponseEntity("Requested quantity not available", HttpStatus.NOT_ACCEPTABLE);
            } else {
                long dbStart = System.currentTimeMillis();
                cartRepository.save(cartItem);
                long dbEnd = System.currentTimeMillis();
                long dbTimeElapsed = dbEnd - dbStart;
                statsd.recordExecutionTime("addBookToCartDBTime", dbTimeElapsed);
                book.setBookQuantity(book.getBookQuantity() - 1);
                book.setBookLastModified(new Date());
                bookRepository.save(book);
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                statsd.recordExecutionTime("addBookToCartApiTime", timeElapsed);
                logger.info("**********Book added to cart**********");
                HttpHeaders headers = new HttpHeaders();
                headers.add("session-id", user.getUuid());
                return ResponseEntity.ok().headers(headers).body(cartItem);
            }
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("addBookToCartApiTime", timeElapsed);
            logger.info("**********Book not found**********");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

}
