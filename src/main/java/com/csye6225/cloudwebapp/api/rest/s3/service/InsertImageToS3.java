/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.cloudwebapp.api.model.Image;
import com.csye6225.cloudwebapp.datasource.repository.ImageRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */

@RestController
@RequestMapping("/v1/uploadImageToS3/bookISBN/{bookISBN}/bookSoldBy/{bookSoldBy}")
public class InsertImageToS3 {
    
    private static final Logger logger = LoggerFactory.getLogger(InsertImageToS3.class);

    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private AmazonS3 amazonS3;
//    private AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
//            .withCredentials(new InstanceProfileCredentialsProvider(true))
//            .build();
    
    @Value("${BUCKET_NAME}")
    private String bucketName;
    
//    private String bucketName = "webapp.snehal.patel";
    
    @Value("${BUCKET_URL}")
    private String bucketUrl;
    
//    private String bucketUrl = "https://s3.us-east-1.amazonaws.com";

    @PostMapping
    @ApiOperation(value = "Inserts image to S3", notes = "Inserts image to S3")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Image inserted to S3 successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 409, message = "Duplicate book entry"),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to insert book details
    public ResponseEntity insertImageToS3(@RequestParam(value = "file") MultipartFile multipartFile,
            @PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "bookSoldBy") String bookSoldBy) throws IOException {
        
        String fileUrl = "";
        Image img = new Image();
        String fileName = multipartFile.getOriginalFilename();

        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileNameWithDate = new Date().getTime()+"-"+fileName.replace(" ", "_");
            fileUrl = bucketUrl+"/"+bucketName+"/"+fileNameWithDate;

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileNameWithDate, file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);

            this.amazonS3.putObject(putObjectRequest);
            
            img.setBookISBN(bookISBN);
            img.setBookSoldBy(bookSoldBy);
            img.setImageURL(fileUrl);
            img.setImageName(fileNameWithDate);
            img.setBookAdded(new Date());
            
            imageRepository.save(img);
            return new ResponseEntity("Image inserted to S3 bucket successfully", HttpStatus.OK);
            
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            return new ResponseEntity("Error inserting image to S3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
        
    }


}
