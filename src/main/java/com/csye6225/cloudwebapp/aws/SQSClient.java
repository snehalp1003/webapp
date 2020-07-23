/**
 * 
 */
package com.csye6225.cloudwebapp.aws;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.csye6225.cloudwebapp.api.model.User;

/**
 * @author Snehal Patel
 *
 */

@Service("amazonSQSService")
public class SQSClient {

    private static final Logger logger = LoggerFactory.getLogger(SQSClient.class);

    @Autowired
    private SNSClient snsClient;

    @Value("${DOMAIN_NAME}")
    private String domainName;

    @Value("${QUEUE}")
    private String sqsQueue;

    private AmazonSQS sqsClient;

    @PostConstruct
    private void init() {
        this.sqsClient = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }
    
    public void sendEmail(String userEmail, String token) {
        try {
            receiveMessageAndDelete();
//            CreateQueueResult create_result = sqsClient.createQueue(sqsQueue);
            String queueUrl = sqsClient.getQueueUrl(sqsQueue).getQueueUrl();
            StringBuilder messageString = new StringBuilder();
            messageString.append(userEmail + ",");
            messageString.append("http://" + domainName + "/reset?email="+ userEmail + "&token=" + token);
            messageString.append(",");
            logger.info("********** Pushed message to queue with key : " + userEmail + " and token : " + messageString.toString() + "**********");
            SendMessageRequest messageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl).withMessageBody(messageString.toString());
            sqsClient.sendMessage(messageRequest);
            receiveMessageAndDelete();
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("********** The queue already exists **********" )) {
                logger.error(exception.getMessage());
                throw exception;
            }
        }
    }
    
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void receiveMessageAndDelete() {
        logger.info("********** Inside receiveMessageAndDelete method **********");
        String queueUrl = sqsClient.getQueueUrl(sqsQueue).getQueueUrl();
        List<Message> receivedMessageList = sqsClient.receiveMessage(sqsClient.getQueueUrl(sqsQueue).getQueueUrl()).getMessages();
        for(Message message : receivedMessageList) {
            if (message.getBody()!= null && !message.getBody().isEmpty()) {
                logger.info("********** Receiving message" + message.getBody() + " **********");
                snsClient.publish(message.getBody());
                sqsClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }
        }
    }
}
