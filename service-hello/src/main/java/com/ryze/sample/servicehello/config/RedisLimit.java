package com.ryze.sample.servicehello.config;

import java.lang.annotation.*;

/**
 * Created by xueLai on 2020/3/20.
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLimit {
    /**
     * 标识参数名, 必须是请求参数中的一个
     */
    String identifier();

    /**
     * 在多长时间内监控, 如希望在 60s 内尝试
     * 次数限制为5次, 那么 watch=60; unit: s
     */
    long watch();

    /**
     * 锁定时长, unit: s
     */
    long lock();

    /**
     * 错误的尝试次数
     */
    int times();
}