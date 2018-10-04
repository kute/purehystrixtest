package com.kute.hystrix.controller;

import com.kute.hystrix.controller.base.BaseController;
import com.netflix.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * created by bailong001 on 2018/10/04 15:45
 *
 * 动态设置hystrix
 */
@RestController
@RequestMapping("/hystrix")
public class HystrixController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HystrixController.class);

    /**
     * 动态设置 属性
     * @param paramMap
     * @return
     */
    @PostMapping("/set")
    public Object dynamicSetProperties(@RequestParam Map<String, String> paramMap) {
        if(paramMap.isEmpty()) {
            return null;
        }
        LOGGER.info("Dynamic set properties:{}", paramMap);
        paramMap.forEach((key, value) -> {
            ConfigurationManager.getConfigInstance().setProperty(key, value);
        });
        return null;
    }

}
