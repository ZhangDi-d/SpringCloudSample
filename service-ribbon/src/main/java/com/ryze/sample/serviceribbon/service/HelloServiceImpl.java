package com.ryze.sample.serviceribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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
    @HystrixCommand(fallbackMethod = "helloError") //在ribbon中使用断路器,该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法
    public String helloService(String name) {
        return restTemplate.getForObject("http://service-hello/hello?name="+name,String.class);
    }

    public String helloError(String name) {
        return "hello,"+name+",sorry,error!";
    }
}
