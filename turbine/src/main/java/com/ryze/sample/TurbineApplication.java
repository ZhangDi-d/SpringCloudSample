package com.ryze.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * Created by xueLai on 2020/2/27.
 */
@SpringBootApplication
@EnableTurbine //开启 turbine'
@EnableDiscoveryClient  //开启服务注册与 发现
@EnableHystrixDashboard //开启 hystrix
public class TurbineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineApplication.class, args);

    }
}
