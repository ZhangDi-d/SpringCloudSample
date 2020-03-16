package com.ryze.sample.servicegateway.filter;

import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Created by xueLai on 2020/3/16.
 * 如果有单独的过滤器,比如Ip过滤器,可以单独写一个过滤器 实现这种复杂的逻辑
 */
@Component
public class IpFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        // 测试使用,测试限流时,放开这段拦截
        if (getIp(headers).equals("127.0.0.2")) {
            ServerHttpResponse response = exchange.getResponse();
            JSONObject jsonObject  = new JSONObject();
            jsonObject.put("code",401);
            jsonObject.put("message","非法请求");
            byte[] datas = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(datas);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
    // 这边从请求头中获取用户的实际IP,根据Nginx转发的请求头获取
    private String getIp(HttpHeaders headers) {
        return "127.0.0.1";
    }
}
