package com.kute.hystrix.config;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCacheAspect;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.strategy.HystrixPlugins;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by bailong001 on 2018/09/30 14:20
 */
@Configuration
@Slf4j
public class HystrixConfig {

    static {
        HystrixPlugins.getInstance().registerCommandExecutionHook(new PureHystricxCommandExecutionHook());
        HystrixPlugins.getInstance().registerEventNotifier(new PureHystricxEventNotifier());
    }

    /**
     * annotation support
     *
     * @return
     */
    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

    /**
     * cache support
     *
     * @return
     */
    @Bean
    public HystrixCacheAspect hystrixCacheAspect() {
        return new HystrixCacheAspect();
    }

    @Bean
    public HystrixMetricsStreamServlet hystrixMetricsStreamServlet() {
        return new HystrixMetricsStreamServlet();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(hystrixMetricsStreamServlet());
        registrationBean.setEnabled(Boolean.TRUE);
        registrationBean.addUrlMappings("/hystrix.stream");
        return registrationBean;
    }

}
