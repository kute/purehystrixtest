package com.kute.hystrix.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * created by bailong001 on 2018/09/30 14:15
 */
@Configuration
public class RestTemplateConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(400);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(200);
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler(3, true);
    }

    @Bean
    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setRetryHandler(httpRequestRetryHandler());
        httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager());
        return httpClientBuilder;
    }

    @Bean
    public HttpClient httpClient() {
        return httpClientBuilder().build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
        httpComponentsClientHttpRequestFactory.setReadTimeout(200);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(5000);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
        return httpComponentsClientHttpRequestFactory;
    }

    @Bean
    public DefaultResponseErrorHandler defaultResponseErrorHandler() {
        return new DefaultResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpComponentsClientHttpRequestFactory());
    }
}