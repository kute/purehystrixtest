package com.kute.hystrix.command;

import com.kute.hystrix.command.base.BaseHystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by bailong001 on 2018/10/03 17:03
 */
public class ExceptionCommand extends BaseHystrixCommand<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCommand.class);

    private Exception exception;

    public ExceptionCommand(Exception exception) {
        this.exception = exception;
    }

    @Override
    protected String run() throws Exception {
        throw exception;
    }

    @Override
    protected String getFallback() {
        LOGGER.info("ExceptionCommand fallback method is executing:{}", exception.toString());
        return "fallback-value";
    }
}
