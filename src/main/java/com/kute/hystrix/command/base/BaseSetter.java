package com.kute.hystrix.command.base;

import com.netflix.hystrix.*;

/**
 * created by bailong001 on 2018/10/02 20:13
 *
 * https://github.com/Netflix/Hystrix/wiki/Configuration
 */
public interface BaseSetter {

    HystrixCommand.Setter setter = HystrixCommand.Setter
            // 命令分组用于对依赖操作分组,便于统计,汇总
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("hystrix.pure.group"))
            // 每个CommandKey代表一个依赖抽象,相同的依赖要使用相同的CommandKey名称,依赖隔离的根本就是对相同CommandKey的依赖做隔离.
            .andCommandKey(HystrixCommandKey.Factory.asKey("hystrix.pure.command"))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("hystrix.pure.threadpool"))
            .andCommandPropertiesDefaults(
                    HystrixCommandProperties.Setter()

                            //设置断路器是否打开的错误请求阀值
                            .withCircuitBreakerRequestVolumeThreshold(2)
                            //设置 在回路被打开，拒绝请求到再次尝试请求并决定回路是否继续打开的时间,单位毫秒，默认为5秒
                            .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
                            //设置 当错误率%达到多少时断路器打开
                            .withCircuitBreakerErrorThresholdPercentage(80)
                            .withFallbackEnabled(true)
                            //隔离策略:固定大小线程池
                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                            //设置是否在超时时开启中断:默认true
                            .withExecutionIsolationThreadInterruptOnTimeout(true)
                            //设置启用超时时间设置:默认 true
                            .withExecutionTimeoutEnabled(true)
                            //执行的超时时间设置
                            .withExecutionTimeoutInMilliseconds(5000)
                    // 使用信号量隔离时，命令调用最大的并发数,默认:10
//                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(100)
            )
            // 线程池配置
            .andThreadPoolPropertiesDefaults(
                    HystrixThreadPoolProperties.Setter()
                            // 核心线程池大小
                            .withCoreSize(10)
                            .withMaximumSize(20)
                            // 配置线程值等待队列长度,默认值:-1,建议值:-1,表示不等待直接拒绝,测试表明线程池使用直接决绝策略+ 合适大小的非回缩线程池效率最高.所以不建议修改此值
                            .withMaxQueueSize(-1)
                            // 线程存活时间：1分钟
                            .withKeepAliveTimeMinutes(1)
            );

    HystrixObservableCommand.Setter observableSetter = HystrixObservableCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("hystrix.pure.group"))
            // 每个CommandKey代表一个依赖抽象,相同的依赖要使用相同的CommandKey名称,依赖隔离的根本就是对相同CommandKey的依赖做隔离.
            .andCommandKey(HystrixCommandKey.Factory.asKey("hystrix.pure.command"))
            .andCommandPropertiesDefaults(
                    HystrixCommandProperties.Setter()

                            //设置断路器是否打开的错误请求阀值
                            .withCircuitBreakerRequestVolumeThreshold(2)
                            //设置 在回路被打开，拒绝请求到再次尝试请求并决定回路是否继续打开的时间,单位毫秒，默认为5秒
                            .withCircuitBreakerSleepWindowInMilliseconds(60 * 1000)
                            //设置 当错误率%达到多少时断路器打开
                            .withCircuitBreakerErrorThresholdPercentage(80)
                            .withFallbackEnabled(true)
                            //隔离策略:固定大小线程池
                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                            //设置是否在超时时开启中断:默认true
                            .withExecutionIsolationThreadInterruptOnTimeout(true)
                            //设置启用超时时间设置:默认 true
                            .withExecutionTimeoutEnabled(true)
                            //执行的超时时间设置
                            .withExecutionTimeoutInMilliseconds(5000)
                    // 使用信号量隔离时，命令调用最大的并发数,默认:10
//                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(100)
            );


    HystrixCollapser.Setter collapserSetter = HystrixCollapser.Setter
            .withCollapserKey(HystrixCollapserKey.Factory.asKey("hystrix.pure.collapser"))
            // REQUEST范围只对一个request请求内的多次服务请求进行合并，GLOBAL是多单个应用中的所有线程的请求中的多次服务请求进行合并
            .andScope(HystrixCollapser.Scope.GLOBAL)
            .andCollapserPropertiesDefaults(
                    HystrixCollapserProperties.Setter()
                            // 批量 大小限制，即 合并的上限
                            .withMaxRequestsInBatch(20)
                            // 时间窗口，即 在此窗口内的请求会被合并为 一个command
                            .withTimerDelayInMilliseconds(500)
                            .withRequestCacheEnabled(true)
            );
}
