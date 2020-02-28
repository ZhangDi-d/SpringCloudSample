package com.ryze.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xueLai on 2020/2/28.
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableTurbineStream //开启turbine流
public class TurbineAmqpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineAmqpApplication.class, args);
    }
}
