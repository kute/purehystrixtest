package com.kute.hystrix.command;

import com.kute.hystrix.command.base.BaseHystrixCollapser;
import com.kute.hystrix.domain.UserData;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * created by bailong001 on 2018/10/03 19:58
 */
public class GetUserCollapser extends BaseHystrixCollapser<List<UserData>, UserData, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserCollapser.class);

    private RestTemplate restTemplate;
    private Long id;

    public GetUserCollapser(RestTemplate restTemplate, Long id) {
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    public Long getRequestArgument() {
        return id;
    }

    /**
     * 将 多个 请求的参数提取，并最后合并为一个command
     *
     * @param collapsedRequests
     * @return
     */
    @Override
    protected HystrixCommand<List<UserData>> createCommand(Collection<CollapsedRequest<UserData, Long>> collapsedRequests) {
        List<Long> idList = collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList());
        LOGGER.info("GetUserCollapser createCommand merge request begin:{}", idList);
        return new BatchGetUserCommand(restTemplate, idList);
    }

    /**
     * 匹配每个请求的响应
     *
     * @param batchResponse
     * @param collapsedRequests
     */
    @Override
    protected void mapResponseToRequests(List<UserData> batchResponse, Collection<CollapsedRequest<UserData, Long>> collapsedRequests) {
        LOGGER.info("GetUserCollapser mapResponseToRequests merge response begin");
        AtomicInteger count = new AtomicInteger(0);
        collapsedRequests.stream().forEach(request -> {
            request.setResponse(batchResponse.get(count.getAndIncrement()));
        });
    }
}
