package com.ryze.sample.servicehello.aop;

import com.ryze.sample.servicehello.config.RedisLimit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by xueLai on 2020/3/20.
 */
@Component
@Aspect
@Slf4j
public class RedisLimitAOP {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(com.ryze.sample.servicehello.config.RedisLimit)")
    public Object handleLimit(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLimit annotation = method.getAnnotation(RedisLimit.class);
        String identifier = annotation.identifier();
        long lock = annotation.lock();
        int times = annotation.times();
        long watch = annotation.watch();

        String identifierValue = null;
        Object arg = proceedingJoinPoint.getArgs()[0];
        try {
            Field declaredField = arg.getClass().getDeclaredField(identifier);
            declaredField.setAccessible(true);
            identifierValue = (String) declaredField.get(arg);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(identifierValue)) {
            log.error(">>> the value of RedisLimit.identifier cannot be blank, invalid identifier: {}", identifier);
        }

        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String flag = stringStringValueOperations.get(identifierValue);
        if (flag != null && "lock".contentEquals(flag)) {
            final BaseResp result = new BaseResp();
            result.setErrMsg("user locked");
            result.setCode("1");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        ResponseEntity result;
        try {
            result = (ResponseEntity) proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            result = handleLoginException(e, identifierValue, watch, times, lock);
        }
        return result;

    }

    private ResponseEntity handleLoginException(Throwable e, String identifierValue, long watch, int times, long lock) {
        final BaseResp result = new BaseResp();
        result.setCode("1");
        if (e instanceof LoginException) {
            log.info(">>> handle login exception...");
            final ValueOperations<String, String> ssOps = stringRedisTemplate.opsForValue();
            Boolean exist = stringRedisTemplate.hasKey(identifierValue);
            // key doesn't exist, so it is the first login failure
            if (exist == null || !exist) {
                ssOps.set(identifierValue, "1", watch, TimeUnit.SECONDS);
                result.setErrMsg(e.getMessage());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            String count = ssOps.get(identifierValue);
            // has been reached the limitation
            if (Integer.parseInt(count) + 1 == times) {
                log.info(">>> [{}] has been reached the limitation and will be locked for {}s", identifierValue, lock);
                ssOps.set(identifierValue, "lock", lock, TimeUnit.SECONDS);
                result.setErrMsg("user locked");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            ssOps.increment(identifierValue);
            result.setErrMsg(e.getMessage() + "; you have try " + ssOps.get(identifierValue) + "times.");
        }
        log.error(">>> RedisLimitAOP cannot handle {}", e.getClass().getName());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
