package com.kute.hystrix.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * created by bailong001 on 2018/09/30 15:45
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setStaticApplicationContext(applicationContext);
    }

    private void setStaticApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
