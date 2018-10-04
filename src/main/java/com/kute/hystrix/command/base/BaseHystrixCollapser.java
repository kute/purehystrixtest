package com.kute.hystrix.command.base;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;

/**
 * created by bailong001 on 2018/10/03 18:14
 */
public abstract class BaseHystrixCollapser<T, R, P> extends HystrixCollapser<T, R, P> implements BaseSetter {

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
