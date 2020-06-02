/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.cart;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.Cart;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.CartRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/refreshCart/bookBoughtBy/{bookBoughtBy}")
public class RefreshCart {
    
    @Autowired
    private CartRepository cartRepository;
    
    @GetMapping
    @ApiOperation(value = "Returns updated cart for a user", notes = "Returns updated cart for a user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated cart successfully"),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to return updated cart
    public ResponseEntity refreshCart(@PathVariable(value = "bookBoughtBy") String bookBoughtBy)
            throws IOException {

        ArrayList<Cart> availableCartItems = cartRepository.findAll();
        
        ArrayList<Cart> returnUpdatedCartItems = new ArrayList<Cart>();
        if (availableCartItems != null && availableCartItems.size() > 0) {
            for (Cart cart : availableCartItems) {
                if (cart.getBookBoughtBy().equals(bookBoughtBy)) {
                    returnUpdatedCartItems.add(cart);
                }
            }
        }
        
        if (returnUpdatedCartItems != null && returnUpdatedCartItems.size() > 0) {
            return new ResponseEntity(returnUpdatedCartItems, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

}
