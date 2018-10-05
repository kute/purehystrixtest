package com.kute.hystrix.command;

import com.kute.hystrix.command.base.BaseHystrixCommand;
import com.kute.hystrix.service.PureService;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by bailong001 on 2018/09/30 15:39
 */
public class SleepCommand extends BaseHystrixCommand<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SleepCommand.class);

    private PureService pureService;
    private Long millis;

    public SleepCommand(PureService pureService, Long millis) {
        super(setter.andCommandKey(HystrixCommandKey.Factory.asKey(SleepCommand.class.getSimpleName())));

        this.pureService = pureService;
        this.millis = millis;
    }

    @Override
    protected Void run() throws Exception {
        LOGGER.info("sleep run execute");
        pureService.sleep(millis);
        return null;
    }

    @Override
    protected Void getFallback() {
        LOGGER.info("sleep fallback execute");
        return null;
    }

}
