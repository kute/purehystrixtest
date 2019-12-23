package com.kute.hystrix.config;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

import java.util.List;

/**
 * created by bailong001 on 2019/12/23 19:34
 */
public class PureHystricxEventNotifier extends HystrixEventNotifier {

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        super.markEvent(eventType, key);
    }

    @Override
    public void markCommandExecution(HystrixCommandKey key, HystrixCommandProperties.ExecutionIsolationStrategy isolationStrategy, int duration, List<HystrixEventType> eventsDuringExecution) {
        super.markCommandExecution(key, isolationStrategy, duration, eventsDuringExecution);
    }
}
