/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.book;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.api.model.Cart;
import com.csye6225.cloudwebapp.api.model.Image;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.CartRepository;
import com.csye6225.cloudwebapp.datasource.repository.ImageRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/deleteBook/bookISBN/{bookISBN}/bookSoldBy/{bookSoldBy}/userLoggedIn/{userLoggedIn}")
public class DeleteBook {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteBook.class);

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private AmazonS3 amazonS3;
    
    @Value("${BUCKET_NAME}")
    private String bucketName;
    
//    private String bucketName = "webapp.snehal.patel";
    
    @DeleteMapping
    @ApiOperation(value = "Deletes book entry specified", notes = "Deletes book entry specified")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book deleted successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to delete book details
    public ResponseEntity deleteBook(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "bookSoldBy") String bookSoldBy,
            @PathVariable(value = "userLoggedIn") String userLoggedIn) throws IOException {

        if(!bookSoldBy.equals(userLoggedIn)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy) != null) {
            Book book = bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy);
            bookRepository.delete(book);
            
            if (cartRepository.findByBookSoldByAndBookISBN(bookSoldBy, bookISBN) != null) {
                ArrayList<Cart> bookToDeleteInCart = cartRepository.findByBookSoldByAndBookISBN(bookSoldBy, bookISBN);
                
                for (Cart bookInCart : bookToDeleteInCart) {
                    cartRepository.delete(bookInCart);
                }
            }
            
            ArrayList<Image> imagesInRepository = imageRepository.findAll();
            
            if(imagesInRepository != null && imagesInRepository.size() > 0) {
                for (Image image : imagesInRepository) {
                    if(image.getBookISBN().equals(bookISBN) && image.getBookSoldBy().equals(bookSoldBy)) {
                        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, image.getImageName()));

                        imageRepository.delete(image);
                    }
                }
            }
            

            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
