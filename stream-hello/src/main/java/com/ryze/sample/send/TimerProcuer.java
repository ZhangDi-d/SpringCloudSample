package com.ryze.sample.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xueLai on 2020/3/3.
 */
@EnableBinding(Source.class)
public class TimerProcuer {
    private static Logger logger = LoggerFactory.getLogger(TimerProcuer.class);
    private final String format  = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "5000", maxMessagesPerPoll = "1"))
    public MessageSource<String> timerMessageSource() {
        logger.info("TimerProcuer sendMessage begin ..........");
        return () -> new GenericMessage<>(new SimpleDateFormat(format).format(new Date()));
    }
}