/**
 * 
 */
package com.csye6225.cloudwebapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author Snehal Patel
 *
 */
@Configuration
public class AWSConfig {
    
    @Bean
    public static AmazonS3Client amazonS3Client() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
    
    

//    private String accessKeyId = "AKIAIGJDIIDOLZX56DJQ";
//    private String secretKey = "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";
//    private String region = "us-east-1";
//    
//    @Bean
//    public BasicAWSCredentials basicAWSCredentials() {
//        return new BasicAWSCredentials(accessKeyId, secretKey);
//    }
//
//    @Bean
//    public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
//        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
//        amazonS3Client.setRegion(Region.getRegion(Regions.fromName(region)));
//        return amazonS3Client;
//    }
}
