package com.ryze.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xueLai on 2020/3/3.
 */
@RestController
public class TraceController {
    private final Logger logger = LoggerFactory.getLogger(TraceController.class);
    private final String RETURN_STR = "trace-2";

    @GetMapping(value = "/trace-2")
    public String trace() {
        logger.info("================trace-2 begin================");
        return RETURN_STR;
    }
}
