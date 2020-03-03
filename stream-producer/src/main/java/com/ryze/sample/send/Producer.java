package com.ryze.sample.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

/**
 * Created by xueLai on 2020/3/2.
 */
@EnableBinding(Source.class)
public class Producer {
    private static Logger logger = LoggerFactory.getLogger(Producer.class);
    @Autowired
    @Output(Source.OUTPUT)
    private MessageChannel channel;

    public void send(String message) {
        logger.info("send massage begin...............................");
        channel.send(MessageBuilder.withPayload("Producer send massage:" + message).build());
        logger.info("send massage end...............................");
    }
}
