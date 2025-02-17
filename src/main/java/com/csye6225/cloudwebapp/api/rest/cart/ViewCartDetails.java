/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.cart;

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
import com.csye6225.cloudwebapp.api.model.Cart;
import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.CartRepository;
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
@RequestMapping("/v1/viewCartDetails/bookBoughtBy/{bookBoughtBy}")
public class ViewCartDetails {
    
    private static final Logger logger = LoggerFactory.getLogger(ViewCartDetails.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;

    @GetMapping
    @ApiOperation(value = "Returns updated cart for a user", notes = "Returns updated cart for a user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated cart successfully"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to return updated cart
    public ResponseEntity viewCartDetails(@PathVariable(value = "bookBoughtBy") String bookBoughtBy)
            throws IOException {
        
        statsd.incrementCounter("viewCartDetailsApi");
        long start = System.currentTimeMillis();
        
        User user = userRepository.findByUserEmailAddress(bookBoughtBy);

        ArrayList<Cart> availableCartItems = cartRepository.findAll();
        
        long dbEnd = System.currentTimeMillis();
        long dbTimeElapsed = dbEnd - start;
        statsd.recordExecutionTime("viewCartDetailsDBTime", dbTimeElapsed);

        ArrayList<Book> returnUpdatedCartItems = new ArrayList<Book>();
        if (availableCartItems != null && availableCartItems.size() > 0) {
            for (Cart cart : availableCartItems) {
                if (cart.getBookBoughtBy().equals(bookBoughtBy)) {
                    returnUpdatedCartItems
                            .add(bookRepository.findByBookISBNAndBookSoldBy(cart.getBookISBN(), cart.getBookSoldBy()));
                }
            }
        }

        if (returnUpdatedCartItems != null && returnUpdatedCartItems.size() > 0) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("viewCartDetailsApiTime", timeElapsed);
            logger.info("**********Fetched cart details**********");
            HttpHeaders headers = new HttpHeaders();
            headers.add("session-id", user.getUuid());
            return ResponseEntity.ok().headers(headers).body(returnUpdatedCartItems);
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("viewCartDetailsApiTime", timeElapsed);
            logger.info("**********Cart is empty**********");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

}
