package com.kute.hystrix.command;

import com.kute.hystrix.command.base.BaseHystrixObservableCommand;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;

/**
 * created by bailong001 on 2018/10/02 20:12
 */
public class GetMultiUserCommand extends BaseHystrixObservableCommand<UserData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMultiUserCommand.class);

    private PureService pureService;
    private List<Long> idList;

    public GetMultiUserCommand(PureService pureService, List<Long> idList) {
        super(observableSetter.andCommandKey(HystrixCommandKey.Factory.asKey(GetMultiUserCommand.class.getSimpleName())));

        this.pureService = pureService;
        this.idList = idList;
    }

    /**
     * 默认 5s 超时
     * @return
     */
    @Override
    protected Observable<UserData> construct() {
        LOGGER.info("GetMultiUserCommand current thread[{}] is running for param={}", Thread.currentThread().getName(), idList);
        return pureService.getMultiUser(idList);
    }

    /**
     * 降级
     *
     * @return
     */
    @Override
    protected Observable<UserData> resumeWithFallback() {
        LOGGER.info("FALLBACK GetMultiUserCommand resumeWithFallback executing:{}", idList);
        return pureService.getMultiUserWithFallBack(idList);
    }
}
