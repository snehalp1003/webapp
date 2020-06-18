///**
// * 
// */
//package com.csye6225.cloudwebapp.api.rest.s3.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.csye6225.cloudwebapp.api.rest.s3.service.S3ImageService;
//
///**
// * @author Snehal Patel
// *
// */
//
//@RestController
//@RequestMapping("/files")
//public S3ImageController {
//
//    @PostMapping
//    public String uploadFileToS3Bucket(@RequestPart(value = "file") MultipartFile file);
//
//    @DeleteMapping
//    public String deleteFileFromS3Bucket(@RequestParam("file_name") String fileName);
//}
