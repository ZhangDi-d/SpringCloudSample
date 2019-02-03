package com.ryze.sample.serviceribbon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by xueLai on 2019/2/3.
 */
@Service("helloService")
public class HelloServiceImpl implements HelloService {
    @Autowired
    RestTemplate restTemplate;

    @Override
    public String helloService(String name) {
        return restTemplate.getForObject("http://service-hello/hello?name="+name,String.class);
    }
}
