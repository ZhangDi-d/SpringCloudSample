package com.ryze.sample.servicehello.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by xueLai on 2020/3/20.
 * 30s 中登录超过30次,不允许登录
 */
@RestController
@RequestMapping
public class LoginController {

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/login")
    public void login(HttpServletRequest request) {
        String ip = "127.0.0.1";
        int initcount = 1;
        Integer ipCount = redisTemplate.opsForValue().get(ip) != null ? (Integer) redisTemplate.opsForValue().get(ip) : 0;
        System.out.println("ipcount:"+ipCount);
        //IP不存在Redis中,缓存
        if (ipCount == 0){
            redisTemplate.opsForValue().set(ip,initcount,1800, TimeUnit.SECONDS);
            System.out.println( "登录成功...");
        }else{
            //已经存在redis中,并且已经超过30次
            if (Integer.valueOf(ipCount)>30){
                System.out.println("已经超过了登录限制次数,请30分钟后重试....");
            }else{
                redisTemplate.opsForValue().set(ip,ipCount+1,1800, TimeUnit.SECONDS);
                System.out.println("登录成功....");
            }
        }
    }
}
