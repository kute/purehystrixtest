package com.kute.hystrix.config;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * created by bailong001 on 2018/10/03 17:15
 */
@Configuration
public class ExceptionConfig {


    @ExceptionHandler(value = {HystrixBadRequestException.class})
    public Object dealHystrixBadRequestException() {
        //TODO deal error
        Map<String, Object> error = new HashMap<>(2);
        error.put("code", 101);
        error.put("message", "illegal");
        return error;
    }

}
