package com.ryze.sample.servicefeign.fallback;

import com.ryze.sample.servicefeign.service.FeignServiceHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * SchedualServiceHelloHystric需要实现SchedualServiceHello 接口，并注入到Ioc容器中
 * Created by xueLai on 2019/2/3.
 */
@Component
public class FeignServiceHelloHystric implements FeignServiceHello {
    private static final Logger logger = LoggerFactory.getLogger(FeignServiceHelloHystric.class);

    @Override
    public String sayHiFromClientOne(String name) {
        logger.error("sayHiFromClientOne 调用异常...");
        return "feign hystrix sayHiFromClientOne==> sorry " + name;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        logger.error("uploadFile 调用异常...");
        return "feign hystrix uploadFile==> sorry " + file.getName();
    }

    @Override
    public String uploadFile2(MultipartFile file) {
        logger.error("uploadFile2 调用异常...");
        return "feign hystrix uploadFile2==> sorry " + file.getName();
    }
}
