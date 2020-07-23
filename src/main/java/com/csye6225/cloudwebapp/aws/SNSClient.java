/**
 * 
 */
package com.csye6225.cloudwebapp.aws;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

/**
 * @author Snehal Patel
 *
 */

@Service("amazonSNSService")
public class SNSClient {

    private static final Logger logger = LoggerFactory.getLogger(SNSClient.class);

    private AmazonSNS snsClient;

    @Value("${TOPIC_ARN}")
    private String topicArn;

    @PostConstruct
    private void init() {
        this.snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }

    public void publish(String message) {
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        final PublishResult publishResponse = snsClient.publish(publishRequest);
        logger.info("**********Message Published ! Message ID :- " + publishResponse.getMessageId() + " **********");
    }
}
