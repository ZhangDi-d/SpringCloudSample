package com.ryze.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by xueLai on 2020/3/3.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Trace1RabbitApplication {
    public static void main(String[] args) {
        SpringApplication.run(Trace1RabbitApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
