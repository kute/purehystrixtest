package com.kute.hystrix.command.base;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
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
public abstract class BaseHystrixCommand<T> extends HystrixCommand<T> implements BaseSetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHystrixCommand.class);

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
