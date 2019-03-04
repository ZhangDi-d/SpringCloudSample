package com.ryze.sample.serviceribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 使用了@EnableDiscoveryClient 注解来修改启动类，该注解使得服务调用
 * 者有能力去 Eureka 中发现服务。需要注意的是，@EnableEurekaClient 注解己经包含了
 * @EnableDiscoveryClient  的功能，也就是说，一个 Eureka 客户端，本身就具有发现服务 的
 * 能力
 */

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient //通过@EnableDiscoveryClient向服务中心注册
@EnableHystrix  //在Ribbon中使用断路器;@EnableHystrix注解开启Hystrix
public class ServiceRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRibbonApplication.class, args);
    }

    /**
     * 向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能
     * @return
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

