package com.ryze.sample.servicefeign.controller;

import com.ryze.sample.servicefeign.service.FeignServiceHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created by xueLai on 2020/2/25.
 */
@RestController
public class UploadConsumerController {
    private static final Logger logger = LoggerFactory.getLogger(UploadConsumerController.class);

    @Resource
    private FeignServiceHello feignServiceHello;

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart("file") MultipartFile file) {
        logger.info("service-feign UploadConsumerController uploadFile begin...");
        String uploadFileName = feignServiceHello.uploadFile(file);
        logger.info("service-feign UploadConsumerController uploadFile end...");
        return uploadFileName;
    }
}
