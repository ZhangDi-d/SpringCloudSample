package com.ryze.sample.servicefeign.fallback;

import com.ryze.sample.servicefeign.service.SchedualServiceHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SchedualServiceHelloHystric需要实现SchedualServiceHello 接口，并注入到Ioc容器中
 * Created by xueLai on 2019/2/3.
 */
@Component
public class SchedualServiceHelloHystric implements SchedualServiceHello {
    private static final Logger logger = LoggerFactory.getLogger(SchedualServiceHelloHystric.class);

    @Override
    public String sayHiFromClientOne(String name) {
        logger.error("sayHiFromClientOne 调用异常...");
        return "feign hystrix ==> sorry " + name;
    }
}
