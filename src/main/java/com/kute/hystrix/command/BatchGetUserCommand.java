package com.kute.hystrix.command;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.kute.hystrix.command.base.BaseHystrixCommand;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created by bailong001 on 2018/10/03 18:37
 */
public class BatchGetUserCommand extends BaseHystrixCommand<List<UserData>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchGetUserCommand.class);

    private RestTemplate restTemplate;
    private List<Long> idList;

    public BatchGetUserCommand(RestTemplate restTemplate, List<Long> idList) {
        super(setter.andCommandKey(HystrixCommandKey.Factory.asKey(BatchGetUserCommand.class.getSimpleName())));
        this.restTemplate = restTemplate;
        this.idList = idList;
    }

    @Override
    protected List<UserData> run() throws Exception {
        LOGGER.info("BatchGetUserCommand batch get:{}", idList);
        String ids = Joiner.on(",").join(idList);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8090/out/getmultiuser/" + ids, String.class);
        return JSONArray.parseArray(responseEntity.getBody(), UserData.class);
    }

    @Override
    protected List<UserData> getFallback() {
        return idList.stream().map(UserData::randUser).collect(Collectors.toList());
    }
}
