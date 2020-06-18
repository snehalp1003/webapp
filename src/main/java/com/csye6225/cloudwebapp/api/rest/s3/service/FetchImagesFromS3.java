/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.s3.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.csye6225.cloudwebapp.api.model.Image;
import com.csye6225.cloudwebapp.datasource.repository.ImageRepository;
import com.csye6225.cloudwebapp.utility.Constants;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */

@RestController
@RequestMapping("/v1/fetchImagesFromS3/bookISBN/{bookISBN}/userLoggedIn/{userLoggedIn}")
public class FetchImagesFromS3 {

    private AmazonS3 amazonS3;
    private static final Logger logger = LoggerFactory.getLogger(FetchImagesFromS3.class);

    @Autowired
    private ImageRepository imageRepository;

    @PostConstruct
    private void initializeAmazon() {
        this.amazonS3 = new AmazonS3Client(new BasicAWSCredentials(Constants.ACCESS_KEY_ID, Constants.SECRET_KEY));
    }

    @GetMapping
    @ApiOperation(value = "Returns list of available images", notes = "Returns list of available images")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Returns list of available images."),
            @ApiResponse(code = 204, message = "No images available for this book."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    public ResponseEntity fetchImagesFromS3(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "userLoggedIn") String userLoggedIn) throws IOException {

        ArrayList<Image> availableImages = imageRepository.findAll();
        ArrayList<Image> validAvailableImages = new ArrayList<Image>();
        
        if (availableImages != null && availableImages.size() > 0) {
            for (Image image : availableImages) {
                if (image.getBookSoldBy().equals(userLoggedIn) && image.getBookISBN().equals(bookISBN)) {
                    validAvailableImages.add(image);
                }
            }
        }
        
        ArrayList<String> returnAvailableImages = new ArrayList<String>();
        
        if(validAvailableImages != null && validAvailableImages.size() > 0) {
            for (Image fetchImg : validAvailableImages) {
              S3Object s3Object = this.amazonS3.getObject(new GetObjectRequest(Constants.BUCKET_NAME, fetchImg.getImageName()));
              String extension = s3Object.getObjectMetadata().getContentType();
              InputStream is = s3Object.getObjectContent();
//              Files.copy(is, Paths.get((new Date()).toString() + s3Object.getKey()));
              File f = new File((new Date()).toString() + s3Object.getKey());
              Files.copy(is, Paths.get(f.getPath()));
              FileInputStream fis = new FileInputStream(f);
//              FileInputStream fis = new FileInputStream(new File((new Date()).toString() + s3Object.getKey()););
              byte[] bytes = new byte[(int) s3Object.getObjectMetadata().getContentLength()];
              fis.read(bytes);
              String encodeBase64 = Base64.getEncoder().encodeToString(bytes);
              String image = "data:"+extension+";base64,"+encodeBase64;
              returnAvailableImages.add(image);
            }
        }
        
        if(returnAvailableImages != null && returnAvailableImages.size() > 0) {
            return new ResponseEntity(returnAvailableImages, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}
