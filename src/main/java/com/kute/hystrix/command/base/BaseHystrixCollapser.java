package com.kute.hystrix.command.base;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;

/**
 * created by bailong001 on 2018/10/03 18:14
 */
public abstract class BaseHystrixCollapser<T, R, P> extends HystrixCollapser<T, R, P> implements IHystrixHandler {

    protected static final HystrixCollapser.Setter collapserSetter = HystrixCollapser.Setter
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

    public BaseHystrixCollapser() {
        super(collapserSetter);
    }

    public BaseHystrixCollapser(HystrixCollapserKey collapserKey) {
        super(collapserKey);
    }

    public BaseHystrixCollapser(Setter setter) {
        super(setter);
    }

}
