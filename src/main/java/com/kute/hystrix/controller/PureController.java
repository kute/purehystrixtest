package com.kute.hystrix.controller;

import com.kute.hystrix.command.GetMultiUserCommand;
import com.kute.hystrix.command.GetUserCollapser;
import com.kute.hystrix.command.GetUserCommand;
import com.kute.hystrix.controller.base.BaseController;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixRequestLog;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func0;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * created by bailong001 on 2018/09/30 15:31
 */
@RestController
@RequestMapping("/pure")
public class PureController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PureController.class);

    @Autowired
    private PureService pureService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getuser/{id}")
    public UserData getUserData(@PathVariable Long id) {

//        // 动态设置值
//        ConfigurationManager.getConfigInstance().setProperty("dynamic.default.sleep.millis", RandomUtils.nextLong(500, 4000));
        if(id == 100) {
            LongStream.range(0, 100).forEach(i -> {
                GetUserCommand command = new GetUserCommand(pureService, i);
                command.execute();
            });
            return null;
        } else {
            GetUserCommand command = new GetUserCommand(pureService, id);
            return command.execute();
        }
    }

    @GetMapping("/getmultiuser/{ids}")
    public List<UserData> getMultiUser(@PathVariable("ids") String ids) {
        LOGGER.info(ids);

        List<Long> idList = splitToList(ids, Long::parseLong);

        List<UserData> resultList = new ArrayList<>(idList.size());

        GetMultiUserCommand command = new GetMultiUserCommand(pureService, idList);

        // hot observable，异步
        Observable<UserData> hotObservable = command.observe();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 同步获取
//        hotObservable.toBlocking().toFuture().get();

        /**
         * 非阻塞
         * 开始订阅，订阅者为 当前对象 即 getMultiUser 订阅 hotObservable，该方法 有很多版本，可以选择性重写 onCompleted,onError,onNext，异步执行
         * 以下为 通知Observer
         */
        Subscription subscription = hotObservable.subscribe(new Observer<UserData>() {
            @Override
            public void onCompleted() {
                LOGGER.info("HAHA Receive onCompleted notice, param={}, finalResult={}", idList, resultList);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                // 异常处理
                throwable.printStackTrace();
            }

            @Override
            public void onNext(UserData userData) {
                LOGGER.info("HAHA Receive onNext notice:", userData.getId());
                resultList.add(userData);
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    /**
     * 请求合并
     * 前 4 个请求，因为请求间隔很短（500ms内），所以会合并在一个线程里，第五个 间隔 1s 会新起一个线程
     * 主要用于 合并来自客户端的请求
     *
     * @return
     */
    @GetMapping("/mergerequest")
    public List<UserData> mergeRequest() {
        List<UserData> resultList = new ArrayList<>();
        List<Future<UserData>> futureList = new ArrayList<>();
        LongStream.rangeClosed(1, 5).boxed().forEach((Long index) -> {
            GetUserCollapser collapser = new GetUserCollapser(restTemplate, index);
            if (index == 5) {
                pureService.sleep(1000);

                UserData userData = collapser.execute();
                LOGGER.info("{} ====> {}", userData.getId(), userData);
                resultList.add(userData);
            } else {
                futureList.add(collapser.queue()); // 这一步 就已经发起请求

                if (futureList.size() == 4) {
                    futureList.forEach(future -> {
                        try {
                            UserData userData = future.get();
                            LOGGER.info("{} ====> {}", userData.getId(), userData);
                            resultList.add(userData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        // 实际执行的命令个数：2
        LOGGER.info("Have already been executed command in fact size:{}", HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        LOGGER.info("Have already been executed command in fact:{}", HystrixRequestLog.getCurrentRequest().getAllExecutedCommands());


        Object deferObservable = Observable.defer(new Func0<Observable<String>>() {
            @Override    //注意此处的call方法没有Subscriber参数
            public Observable<String> call() {
                return Observable.just("deferObservable");
            }
        });

        return resultList;
    }


}
