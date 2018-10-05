package com.kute.hystrix.command.base;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;

/**
 * created by bailong001 on 2018/09/30 17:52
 * <p>
 * HystrixObservableCommand：用于 依赖服务返回多个操作结果
 * <p>
 * 1、HystrixCommand提供了同步和异步两种执行方式，而HystrixObservableCommand只有异步方式
 * 2、HystrixCommand的run方法是用内部线程池的线程来执行的，而HystrixObservableCommand则是由调用方(例如Tomcat容器)的线程来执行的
 * 3、HystrixCommand一次只能发送单条数据返回，而HystrixObservableCommand一次可以发送多条数据返回
 */
public abstract class BaseHystrixObservableCommand<T> extends HystrixObservableCommand<T> implements IHystrixHandler {

    protected static final HystrixObservableCommand.Setter observableSetter = HystrixObservableCommand.Setter
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

    protected BaseHystrixObservableCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected BaseHystrixObservableCommand(Setter setter) {
        super(setter);
    }

    public BaseHystrixObservableCommand() {
        super(observableSetter);
    }

    /**
     * 当 observe() 或者 toObserve() 方法被调用时执行
     * <p>
     * 1、observe()：返回的是 hot observable，当 ovserve(）方法被调用时，命令立即执行，当该方法返回的对象被重复订阅时，会重放之前的行为
     * 2、toObserve(): 返回的是 cold observable，toObserve() 被调用时不会立即执行，只有当 所有的订阅者都订阅之后才会执行
     * 如何判定 所有的订阅者都订阅了 ？
     *
     * @return
     */
    @Override
    protected abstract Observable<T> construct();

    /**
     * 1、当 ovserve() 或者 toObserve() 方法 执行失败时 被调用，即 fallback
     * 2、降级实现 应为 不涉及到网络操作，如 缓存，静态方法
     * 3、若 有必要 通过网络操作，则应该 调用 另一个 不依赖网络的 HystrixObservableCommand 才好
     *
     * @return
     */
    @Override
    protected abstract Observable<T> resumeWithFallback();

    @Override
    protected boolean isFallbackUserDefined() {
        return super.isFallbackUserDefined();
    }

    /**
     * 请求缓存
     *
     * @return
     */
    @Override
    protected String getCacheKey() {
        return super.getCacheKey();
    }

}
