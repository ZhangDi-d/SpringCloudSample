package com.ryze.sample.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Created by xueLai on 2020/2/26.
 */

@EnableDiscoveryClient //注册为服务 ,供euraka 调用
@EnableConfigServer //开启Spring Cloud Config的服务端功能
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
