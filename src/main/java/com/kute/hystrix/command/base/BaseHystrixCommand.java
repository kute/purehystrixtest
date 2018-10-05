package com.kute.hystrix.command.base;

import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kute on 2017/12/9.
 * <p>
 * execute()：同步执行
 * queue()：异步执行
 * <p>
 * HystrixCommand：返回单个操作结果
 */
public abstract class BaseHystrixCommand<T> extends HystrixCommand<T> implements IHystrixHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHystrixCommand.class);

    protected static final HystrixCommand.Setter setter = HystrixCommand.Setter
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

    public BaseHystrixCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    public BaseHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(group, threadPool);
    }

    public BaseHystrixCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
    }

    public BaseHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
    }

    public BaseHystrixCommand(Setter setter) {
        super(setter);
    }

    public BaseHystrixCommand() {
        super(setter);
    }

    /**
     * 除了 HystrixBadRequestException 异常，只要 run 方法抛出异常，就执行 降级 fallback，
     * HystrixBadRequestException用在非法参数或非系统故障异常等不应触发降级逻辑的场景
     *
     * @return
     * @throws Exception
     */
    @Override
    protected abstract T run() throws Exception;

    /**
     * 当熔断机制触发,会调用此方法返回结果
     *
     * @return
     */
    @Override
    protected abstract T getFallback();

    /**
     * 请求缓存，返回null表示不缓存
     * 请求缓存可以让(CommandKey/CommandGroup)相同的情况下,直接共享结果，降低依赖调用次数，在高并发和CacheKey碰撞率高场景下可以提升性能
     *
     * @return
     */
    @Override
    protected String getCacheKey() {
        return super.getCacheKey();
    }
}
