package com.ryze.sample.serviceribbon.controller;

import com.ryze.sample.serviceribbon.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xueLai on 2019/2/3.
 */
@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    @Autowired
    HelloService helloService;

    @GetMapping(value = "/hello")
    public String hi(@RequestParam String name) {
        logger.info("ribbon 调用hello接口 begin...");
        String message = helloService.helloService(name);
        logger.info("ribbon 调用hello接口 end...");
        return message;
    }
}
