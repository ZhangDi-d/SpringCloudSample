package com.ryze.sample.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by xueLai on 2020/3/3.
 */
@RestController
@RequestMapping
public class ProduceController {
    private static Logger logger = LoggerFactory.getLogger(ProduceController.class);
    @Resource
    private Producer producer;

    @RequestMapping("/send")
    public void sendMessage(String message) {
        producer.send("ProduceController send message:" + message);
    }
}
