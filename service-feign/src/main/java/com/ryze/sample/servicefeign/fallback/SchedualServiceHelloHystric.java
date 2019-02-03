package com.ryze.sample.servicefeign.fallback;

import com.ryze.sample.servicefeign.service.SchedualServiceHello;
import org.springframework.stereotype.Component;

/**
 * SchedualServiceHelloHystric需要实现SchedualServiceHello 接口，并注入到Ioc容器中
 * Created by xueLai on 2019/2/3.
 */
@Component
public class SchedualServiceHelloHystric implements SchedualServiceHello {
    @Override
    public String sayHiFromClientOne(String name) {
        return "feign hystrix ==> sorry "+name;
    }
}
