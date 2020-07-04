/**
 * 
 */
package com.csye6225.cloudwebapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author Snehal Patel
 *
 */
//@Configuration
//public class AWSConfig {

//    @Value("${AWS_ACCESS_KEY_ID}")
//    private String accessKeyId;
    
//    private String accessKeyId = "AKIAIGJDIIDOLZX56DJQ";
    
//    @Value("${AWS_SECRET_ACCESS_KEY}")
//    private String secretKey;
    
//    private String secretKey = "lxXw3FGZBLHrUw5o/hbED0jixnBYDdE+wx1gyjWM";

//    @Value("${AWS_REGION}")
//    private String region;
    
//    private String region = "us-east-1";

//    @Bean
//    public BasicAWSCredentials basicAWSCredentials() {
//        return new BasicAWSCredentials(accessKeyId, secretKey);
//    }

//    @Bean
//    public AmazonS3 amazonS3() {
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new InstanceProfileCredentialsProvider(true))
//                .withRegion(Regions.fromName(region))
//                .build();
//        AmazonS3Client amazonS3Client = (AmazonS3Client) AmazonS3ClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(true)).build();
//        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
//        amazonS3Client.setRegion(Region.getRegion(Regions.fromName(region)));
//        return s3Client;
//    }
//}
