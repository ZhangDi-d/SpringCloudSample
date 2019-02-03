package com.ryze.sample.servicefeign.controller;

import com.netflix.discovery.converters.Auto;
import com.ryze.sample.servicefeign.service.SchedualServiceHello;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xueLai on 2019/2/3.
 */
@RestController
public class HelloController {
    // //编译器报错，无视。 因为这个Bean是在程序启动的时候注入的，编译器感知不到，所以报错。
    @Autowired
    SchedualServiceHello schedualServiceHello;

    //必须加上@RequestParam(value = "name"),否则可能会失败
    @GetMapping("/hello")
    public String sayHello(@RequestParam String name){
        return schedualServiceHello.sayHiFromClientOne(name);
    }
}
