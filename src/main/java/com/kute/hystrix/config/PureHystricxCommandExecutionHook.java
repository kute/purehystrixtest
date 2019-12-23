package com.kute.hystrix.config;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import lombok.extern.slf4j.Slf4j;

/**
 * created by bailong001 on 2019/12/23 19:27
 * 降级的监控
 */
@Slf4j
public class PureHystricxCommandExecutionHook extends HystrixCommandExecutionHook {

    @Override
    public <T> void onStart(HystrixInvokable<T> commandInstance) {
        super.onStart(commandInstance);
    }

    @Override
    public <T> T onEmit(HystrixInvokable<T> commandInstance, T value) {
        return super.onEmit(commandInstance, value);
    }

    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, HystrixRuntimeException.FailureType failureType, Exception e) {
        return super.onError(commandInstance, failureType, e);
    }

    @Override
    public <T> void onSuccess(HystrixInvokable<T> commandInstance) {
        super.onSuccess(commandInstance);
    }

    @Override
    public <T> void onThreadStart(HystrixInvokable<T> commandInstance) {
        super.onThreadStart(commandInstance);
    }

    @Override
    public <T> void onThreadComplete(HystrixInvokable<T> commandInstance) {
        super.onThreadComplete(commandInstance);
    }

    @Override
    public <T> void onExecutionStart(HystrixInvokable<T> commandInstance) {
        super.onExecutionStart(commandInstance);
    }

    @Override
    public <T> T onExecutionEmit(HystrixInvokable<T> commandInstance, T value) {
        return super.onExecutionEmit(commandInstance, value);
    }

    @Override
    public <T> Exception onExecutionError(HystrixInvokable<T> commandInstance, Exception e) {
        return super.onExecutionError(commandInstance, e);
    }

    @Override
    public <T> void onExecutionSuccess(HystrixInvokable<T> commandInstance) {
        super.onExecutionSuccess(commandInstance);
    }

    @Override
    public <T> void onFallbackStart(HystrixInvokable<T> commandInstance) {
        HystrixCommand hystrixCommand = (HystrixCommand) commandInstance;
        String commandKey = hystrixCommand.getCommandKey().toString();
        log.error("Hystrix: {} 接口开始降级", commandKey);
        super.onFallbackStart(commandInstance);
    }

    @Override
    public <T> T onFallbackEmit(HystrixInvokable<T> commandInstance, T value) {
        return super.onFallbackEmit(commandInstance, value);
    }

    @Override
    public <T> Exception onFallbackError(HystrixInvokable<T> commandInstance, Exception e) {
        return super.onFallbackError(commandInstance, e);
    }

    @Override
    public <T> void onFallbackSuccess(HystrixInvokable<T> commandInstance) {
        super.onFallbackSuccess(commandInstance);
    }

    @Override
    public <T> void onCacheHit(HystrixInvokable<T> commandInstance) {
        super.onCacheHit(commandInstance);
    }

    @Override
    public <T> void onUnsubscribe(HystrixInvokable<T> commandInstance) {
        super.onUnsubscribe(commandInstance);
    }
}
