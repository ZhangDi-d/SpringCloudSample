package com.ryze.sample.servicefeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by xueLai on 2019/2/3.
 */
@FeignClient(value = "service-hello")
public interface SchedualServiceHello {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
