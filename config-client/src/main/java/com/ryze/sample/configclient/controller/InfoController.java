package com.ryze.sample.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xueLai on 2020/2/26.
 */
@RestController
public class InfoController {
    @Value("${info.profile}")
    String profile;

    @Value("${info.from}")
    String from;

    @Value("${info.secretValue}") //测试加密解密
            String secretValue;

    @GetMapping(value = "/getInfo")
    public String getInfo() {
        return "InfoController getInfo===============>profile=" + profile + ",from=" + from + ",secretValue=" + secretValue;
    }
}
