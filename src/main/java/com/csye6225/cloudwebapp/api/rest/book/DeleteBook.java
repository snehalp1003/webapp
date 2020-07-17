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
import org.springframework.http.HttpHeaders;
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
import com.csye6225.cloudwebapp.api.model.User;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;
import com.csye6225.cloudwebapp.datasource.repository.CartRepository;
import com.csye6225.cloudwebapp.datasource.repository.ImageRepository;
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
    private UserRepository userRepository;
    
    @Autowired
    private StatsDClient statsd;
    
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
        
        statsd.incrementCounter("deleteBookApi");
        long start = System.currentTimeMillis();
        
        User user = userRepository.findByUserEmailAddress(userLoggedIn);
        if (user == null || !UtilityService.checkStringNotNull(user.getUuid())) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("deleteBookApiTime", timeElapsed);
            logger.info("**********Session expired for user**********");
            return new ResponseEntity("Session expired for user.", HttpStatus.REQUEST_TIMEOUT);
        }

        if(!bookSoldBy.equals(userLoggedIn)) {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("deleteBookApiTime", timeElapsed);
            logger.info("**********User does not have permission to delete book !**********");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } else if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy) != null) {
            long dbBookDeleteStart = System.currentTimeMillis();
            Book book = bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookSoldBy);
            bookRepository.delete(book);
            long dbBookDeleteEnd = System.currentTimeMillis();
            long dbBookDeleteTimeElapsed = dbBookDeleteEnd - dbBookDeleteStart;
            statsd.recordExecutionTime("deleteBookFromDBTime", dbBookDeleteTimeElapsed);
            
            if (cartRepository.findByBookSoldByAndBookISBN(bookSoldBy, bookISBN) != null) {
                ArrayList<Cart> bookToDeleteInCart = cartRepository.findByBookSoldByAndBookISBN(bookSoldBy, bookISBN);
                
                for (Cart bookInCart : bookToDeleteInCart) {
                    long dbBookDeleteFromCartStart = System.currentTimeMillis();
                    cartRepository.delete(bookInCart);
                    long dbBookDeleteFromCartEnd = System.currentTimeMillis();
                    long dbBookDeleteFromCartTimeElapsed = dbBookDeleteFromCartEnd - dbBookDeleteFromCartStart;
                    statsd.recordExecutionTime("deleteBookFromCartDBTime", dbBookDeleteFromCartTimeElapsed);
                }
            }
            
            ArrayList<Image> imagesInRepository = imageRepository.findAll();
            
            if(imagesInRepository != null && imagesInRepository.size() > 0) {
                for (Image image : imagesInRepository) {
                    if(image.getBookISBN().equals(bookISBN) && image.getBookSoldBy().equals(bookSoldBy)) {
                        long dbBookImageDeleteFromS3Start = System.currentTimeMillis();
                        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, image.getImageName()));
                        long dbBookImageDeleteFromS3End = System.currentTimeMillis();
                        long dbBookImageDeleteFromS3TimeElapsed = dbBookImageDeleteFromS3End - dbBookImageDeleteFromS3Start;
                        statsd.recordExecutionTime("deleteBookImageFromS3Time", dbBookImageDeleteFromS3TimeElapsed);
                        imageRepository.delete(image);
                    }
                }
            }
            
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("deleteBookApiTime", timeElapsed);
            logger.info("**********Book deleted from database !**********");
            HttpHeaders headers = new HttpHeaders();
            headers.add("session-id", user.getUuid());
            return ResponseEntity.ok().headers(headers).body(book);
        } else {
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            statsd.recordExecutionTime("deleteBookApiTime", timeElapsed);
            logger.info("**********Book not found !**********");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }
}
