package com.kute.hystrix.command.base;

import com.netflix.hystrix.HystrixCommandGroupKey;
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
public abstract class BaseHystrixObservableCommand<T> extends HystrixObservableCommand<T> implements BaseSetter {

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
