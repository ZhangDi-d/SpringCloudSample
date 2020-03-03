package com.ryze.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Created by xueLai on 2020/3/3.
 */
@RestController
public class TraceController {
    private final Logger logger = LoggerFactory.getLogger(TraceController.class);
    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/trace-1")
    public String trace() {
        logger.info("================trace-1 begin================");
        return restTemplate.getForEntity("http://trace-2/trace-2", String.class).getBody();
    }
}
