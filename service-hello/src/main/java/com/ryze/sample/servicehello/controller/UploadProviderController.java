package com.ryze.sample.servicehello.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by xueLai on 2020/2/25.
 * 接收文件的controller--文件上传服务的提供者
 */
@RestController
@RequestMapping("/file")
public class UploadProviderController {
    private static final Logger logger = LoggerFactory.getLogger(UploadProviderController.class);

    /**
     * @return 文件名
     * @RequestPart这个注解用在multipart/form-data表单提交请求的方法上。
     * @RequestParam也同样支持multipart/form-data请求。他们最大的不同是，当请求方法的请求参数类型不再是String类型的时候。
     * @RequestParam适用于name-valueString类型的请求域，@RequestPart适用于复杂的请求域（像JSON，XML）
     */
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        logger.info("uploadFile:" + file.getSize());
        return file.getName();
    }

    @PostMapping(value = "/uploadFile2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile2(MultipartFile file) {
        logger.info("uploadFile2:" + file.getSize());
        return file.getName();
    }

}
