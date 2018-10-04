package com.kute.hystrix.config;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCacheAspect;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * created by bailong001 on 2018/09/30 14:20
 */
@Configuration
public class HystrixConfig {

    /**
     * annotation support
     * @return
     */
    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

    /**
     * cache support
     * @return
     */
    @Bean
    public HystrixCacheAspect hystrixCacheAspect() {
        return new HystrixCacheAspect();
    }

}
