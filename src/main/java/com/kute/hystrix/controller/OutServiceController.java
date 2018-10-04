package com.kute.hystrix.controller;

import com.kute.hystrix.command.GetUserCommand;
import com.kute.hystrix.controller.base.BaseController;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created by bailong001 on 2018/10/03 20:04
 * <p>
 * 模拟外部服务
 */
@RestController
@RequestMapping("/out")
public class OutServiceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutServiceController.class);

    @Autowired
    private PureService pureService;

    @GetMapping("/getuser/{id}")
    public UserData getUserData(@PathVariable Long id) {
        return pureService.getDataAfterSleep(id, RandomUtils.nextLong(100, 5000));
    }

    @GetMapping("/getmultiuser/{ids}")
    public List<UserData> getMultiUser(@PathVariable("ids") String ids) {
        List<Long> idList = splitToList(ids, Long::parseLong);
        List<UserData> resultList = idList.stream().map(id -> pureService.getDataAfterSleep(id, RandomUtils.nextLong(100, 5000))).collect(Collectors.toList());
        LOGGER.info("OutServiceController getMultiUser idList={}, resultList={}", idList, resultList);
        return resultList;
    }


}
