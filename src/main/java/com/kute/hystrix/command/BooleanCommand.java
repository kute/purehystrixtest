package com.kute.hystrix.command;

import com.kute.hystrix.command.base.BaseHystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;

/**
 * created by bailong001 on 2018/11/01 17:04
 */
public class BooleanCommand extends BaseHystrixCommand<Boolean> {

    public BooleanCommand() {
        super(setter.andCommandKey(HystrixCommandKey.Factory.asKey(BooleanCommand.class.getSimpleName())));
    }

    @Override
    protected Boolean run() throws Exception {
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
