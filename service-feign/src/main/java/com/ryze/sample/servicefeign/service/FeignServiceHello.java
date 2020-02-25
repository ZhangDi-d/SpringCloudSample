package com.ryze.sample.servicefeign.service;

import com.ryze.sample.servicefeign.config.FeignSupportConfig;
import com.ryze.sample.servicefeign.fallback.FeignServiceHelloHystric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

/**
 * feign使用hystrix 只需要在FeignClient的SchedualServiceHello接口的注解中加上fallback的指定类
 * Created by xueLai on 2019/2/3.
 */

@FeignClient(value = "service-hello", fallback = FeignServiceHelloHystric.class,configuration = FeignSupportConfig.class) //通过@ FeignClient（“服务名”），来指定调用哪个服务
public interface FeignServiceHello {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);

    @RequestMapping(value = "/file/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file);

    @RequestMapping(value = "/file/uploadFile2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile2(MultipartFile file);
}
