package com.mcamelo.challengeanota.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AwsSqsConfig {

    @Value("${aws.region}")
    private String region;
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secret_access_key}")
    private String secretKey;
    @Value("${aws.sqs.catalog.arn}")
    private String sqsCatalogArn;
    @Value("${aws.sqs.catalog.url}")
    private String sqsCatalogUrl;

    private AWSCredentials awsCredentials(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
            return credentials;
    }
    private AmazonSQS sqsClientBuilder(){
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials())).withRegion(region)
                .build();
        return sqs;
    }

    public void createSQSQueue(String queueName){
        AmazonSQS sqsClient = sqsClientBuilder();
        CreateQueueRequest queueRequest = new CreateQueueRequest(queueName);
        String standardQueueUrl = sqsClient.createQueue(queueRequest).getQueueUrl();
        System.out.println("AWS SQS QUEUE URL: "+standardQueueUrl);
    }
    public String produceMessageToSQS(String message){
        AmazonSQS sqsClient = sqsClientBuilder();
        SendMessageRequest request = new SendMessageRequest().withQueueUrl(sqsCatalogUrl).withMessageBody(message).withDelaySeconds(10);
        return sqsClient.sendMessage(request).getMessageId();
    }

    public List<Message> consumeMessageFromSQS(){
        AmazonSQS sqsClient = sqsClientBuilder();
        ReceiveMessageRequest request = new ReceiveMessageRequest(sqsCatalogUrl).withWaitTimeSeconds(10).withMaxNumberOfMessages(10);

        List<Message> sqsMessages = sqsClient.receiveMessage(request).getMessages();
        for(Message message : sqsMessages){
            System.out.println(message.getBody());
            System.out.println(extractMessageFromJson(message.getBody()));
        }
        return sqsMessages;
    }

    public void dequeueMessageFromSQS(Message message){
        AmazonSQS sqsClient = sqsClientBuilder();
        sqsClient.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(sqsCatalogUrl).withReceiptHandle(message.getReceiptHandle()));
    }
    public void printUrls(){
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        ListQueuesResult lq_result = sqs.listQueues();
        System.out.println("Your SQS Queue URLs:");
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);
        }
    }
    public static String extractMessageFromJson(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Acessa o campo "Message"
            String message = rootNode.path("Message").asText();

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // ou lançar uma exceção, dependendo do seu cenário
        }
    }
}
