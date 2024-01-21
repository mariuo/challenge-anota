package com.mcamelo.challengeanota.config.aws;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SqsMessageListener {
    @SqsListener("${sqsQueueName}")
//    @SqsListener("catalog-update")
    public void queueListener(String message) {
        try {
            log.info("Consumer SQS : "+message);
            System.out.println(message);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
