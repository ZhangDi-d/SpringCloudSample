package com.ryze.sample.servicefeign.service;

import com.ryze.sample.servicefeign.fallback.SchedualServiceHelloHystric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * feign使用hystrix 只需要在FeignClient的SchedualServiceHello接口的注解中加上fallback的指定类
 * Created by xueLai on 2019/2/3.
 */

@FeignClient(value = "service-hello",fallback = SchedualServiceHelloHystric.class)
public interface SchedualServiceHello {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
