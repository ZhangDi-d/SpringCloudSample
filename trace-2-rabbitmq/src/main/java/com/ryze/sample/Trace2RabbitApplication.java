package com.ryze.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by xueLai on 2020/3/3.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Trace2RabbitApplication {
    public static void main(String[] args) {
        SpringApplication.run(Trace2RabbitApplication.class, args);
    }
}