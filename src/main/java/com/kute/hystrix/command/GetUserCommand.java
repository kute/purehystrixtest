package com.kute.hystrix.command;

import com.google.common.base.Joiner;
import com.kute.hystrix.command.base.BaseHystrixCommand;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import com.kute.hystrix.util.PureConstant;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * created by bailong001 on 2018/09/30 14:24
 */
public class GetUserCommand extends BaseHystrixCommand<UserData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserCommand.class);

    private PureService pureService;
    private Long id;

    public GetUserCommand(PureService pureService, Long id) {
        super(setter.andCommandKey(HystrixCommandKey.Factory.asKey(GetUserCommand.class.getSimpleName())));

        this.pureService = pureService;
        this.id = id;
    }

    @Override
    protected UserData run() throws Exception {
        // 动态获取配置
        Long millis = this.getProperty(PureConstant.DYNAMIC_GETUSER_COMMAND_KEY, 6000L);
        LOGGER.info("GetUserCommand is executing for param={}, sleep={}", id, millis);
        return pureService.getDataAfterSleep(id, millis);
    }

    /**
     * 降级
     *
     * @return
     */
    @Override
    protected UserData getFallback() {
        return new UserData(-1L, "fallback-name", new Date());
    }

    /**
     * 开启 请求缓存
     *
     * @return
     */
    @Override
    protected String getCacheKey() {
        return getCacheKey(this.id);
    }

    /**
     * 缓存刷新
     *
     * @param id
     */
    public static void flushCache(Long id) {
        HystrixRequestCache.getInstance(HystrixCommandKey.Factory.asKey("hystrix.pure.command"), HystrixConcurrencyStrategyDefault.getInstance())
                .clear(getCacheKey(id));
    }

    private static String getCacheKey(Long id) {
        return Joiner.on("_").join(GetUserCommand.class.getSimpleName(), id);
    }
}
