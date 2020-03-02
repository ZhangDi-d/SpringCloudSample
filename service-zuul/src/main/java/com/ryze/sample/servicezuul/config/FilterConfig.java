package com.ryze.sample.servicezuul.config;

import com.ryze.sample.servicezuul.filter.MyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xueLai on 2020/3/2.
 *
 */
@Configuration
public class FilterConfig {

    //配置过滤器,否则不会生效
    @Bean
    public MyFilter myFilter() {
        return new MyFilter();
    }
}
