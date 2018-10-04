package com.kute.hystrix.service;

import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.util.CacheUtil;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.observables.AsyncOnSubscribe;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * created by bailong001 on 2018/09/30 15:28
 */

/**
 * 优先级：
 * 1、使用@HystrixCommand的fallbackMethod属性定义命令回退
 * 2、使用@HystrixCommand的defaultFallback属性定义命令默认回退
 * 3、使用@DefaultProperties的defaultFallback属性定义类默认回退
 */
@DefaultProperties(defaultFallback = "defaultFallback") // 默认降级方法，不允许有其他参数
@Service
public class PureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PureService.class);

    @Autowired
    private RestTemplate restTemplate;

    public void sleep(long millis) {
        LOGGER.info("sleep:{}", millis);
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            // 本该 休眠 millis 这么长时间的，但是 中途如果被其他线程唤醒打断，那么就抛出此异常，这里指被hystrix打断，因为超时时间到了，要去执行降级方法
            e.printStackTrace();
        }
    }

    public void throwException() {
        throw new RuntimeException("exception");
    }

    public UserData getDataAfterSleep(long id, long millis) {
        sleep(millis);
        return UserData.randUser(id);
    }

    public Observable<UserData> getMultiUser(List<Long> idList) {

        if (CollectionUtils.isEmpty(idList)) {
            return Observable.empty();
        }
        /**
         * 响应式编程，rxjava，观察者模式
         * 以下 为 Observable 的执行
         */
        return Observable.create((Observable.OnSubscribe<UserData>) subscriber -> {
            // request for other api on network

            // 判定是否取消订阅
            if (!subscriber.isUnsubscribed()) {
                for (Long id : idList) {

                    ResponseEntity<UserData> responseEntity = restTemplate.getForEntity("http://localhost:8090/pure/getuser/" + id, UserData.class);
                    if (null != responseEntity && responseEntity.getStatusCode() == HttpStatus.OK) {
                        UserData userData = responseEntity.getBody();

                        // 模拟 网络抖动超时，以便执行降级
                        try {
                            Long r = RandomUtils.nextLong(0, 10);
                            LOGGER.info("sleeping in {} seconds for id={}", r, id);
                            TimeUnit.SECONDS.sleep(r);
                        } catch (InterruptedException e) {
                            // 向 observer 通知 onError 通知
                            subscriber.onError(e);
                        }
                        // 模拟 结束

                        // 向 observer 发送通知，即 向PureController.getMultiUser 中的 onNext 接收onNext通知
                        LOGGER.info("HAHA send onNext notice:{}", id);
                        subscriber.onNext(userData);
                    }
                }
                // 向 observer 发送 onCompleted 通知
                LOGGER.info("HAHA send onCompleted notice:{}", idList);
                subscriber.onCompleted();
            }

            /**
             * SubscribeOn操作符指派了Observable的执行线程，这里指派 执行 在 IO线程上（读写文件、数据库、网络请求等），与newThread()差不多，区别在于io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程
             *
             * io():
             * trampoline(): 主要用于延迟工作任务的执行。当我们想在当前线程执行一个任务时，并不是立即，我们可以用.trampoline()将它入队,它是repeat()和retry()方法默认的调度器
             * immediate(): 主要用于立即在当前线程执行你指定的工作。它是timeout(),timeInterval(),以及timestamp()方法默认的调度器
             * computation(): 计算所使用的调度器。这个计算指的是 CPU 密集型计算，即不会被 I/O等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数
             * newThread(): 开启新线程
             *
             */
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 降级 从本地缓存取
     *
     * @param idList
     * @return
     */
    public Observable<UserData> getMultiUserWithFallBack(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Observable.empty();
        }
        return Observable.create((Observable.OnSubscribe<UserData>) subscriber -> {
            // get cache data
            if (!subscriber.isUnsubscribed()) {
                for (Long id : idList) {
                    UserData userData = CacheUtil.getFromLocalCache(id);
                    subscriber.onNext(userData);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.immediate());
    }

    /**
     * 同步执行
     * <p>
     * <p>
     * 支持的类型：
     * 1、sync command with sync fallback
     * 2、async command with sync fallback
     * 3、async command with async fallback
     *
     * @param id
     * @return
     */
    @HystrixCommand(groupKey = "pureServiceGroup", commandKey = "getUserDataCommandSync", threadPoolKey = "userDataPoll",
            commandProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "80"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "60000"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
                    @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "10"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAXIMUM_SIZE, value = "20"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "-1"),
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "1")
            },
            fallbackMethod = "getUserDataFromOutServiceFallBack"
    )
    public UserData getUserDataFromOutServiceSync(Long id) {
        if (null == id) {
            return null;
        }
        ResponseEntity<UserData> responseEntity = restTemplate.getForEntity("http://localhost:8090/out/getuser/" + id, UserData.class);
        // 模拟 网络抖动
        sleep(RandomUtils.nextLong(50, 10000));
        return responseEntity.getBody();
    }

    /**
     * 异步:返回Future
     *
     * @param id
     * @return
     */
    @HystrixCommand(groupKey = "pureServiceGroup", commandKey = "getUserDataCommandAsync", threadPoolKey = "userDataPoll",
            commandProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "80"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "60000"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
                    @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "10"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAXIMUM_SIZE, value = "20"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "-1"),
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "1")
            },
            fallbackMethod = "getUserDataFromOutServiceFallBack"
    )
    public Future<UserData> getUserDataFromOutServiceAsync(Long id) {
        if (null == id) {
            return null;
        }
        return new AsyncResult<UserData>() {
            @Override
            public UserData invoke() {
                ResponseEntity<UserData> responseEntity = restTemplate.getForEntity("http://localhost:8090/out/getuser/" + id, UserData.class);
                // 模拟 网络抖动
                sleep(RandomUtils.nextLong(50, 10000));
                return responseEntity.getBody();
            }
        };
    }

    /**
     * 响应式
     *
     * @param id
     * @return
     */
    @HystrixCommand(groupKey = "pureServiceGroup", commandKey = "getUserDataCommandReactive", threadPoolKey = "userDataPoll",
            commandProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "80"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "60000"),
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
                    @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "10"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAXIMUM_SIZE, value = "20"),
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "-1"),
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "1")
            },
            fallbackMethod = "getUserDataFromOutServiceFallBack"
    )
    public Observable<UserData> getUserDataFromOutServiceReactive(Long id) {
        if (null == id) {
            return null;
        }
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                try {
                    ResponseEntity<UserData> responseEntity = restTemplate.getForEntity("http://localhost:8090/out/getuser/" + id, UserData.class);
                    // 模拟 网络抖动
                    sleep(RandomUtils.nextLong(50, 10000));
                    UserData userData = responseEntity.getBody();

                    subscriber.onNext(userData);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 降级 方法
     *
     * @param id
     * @param e  引发降级异常，为扩展参数
     * @return
     */
    public UserData getUserDataFromOutServiceFallBack(Long id, Throwable e) {
        LOGGER.info("getUserDataFromOutServiceFallBack is executing:{}", id);
        return UserData.randUser(id);
    }

    /**
     * 默认降级策略
     *
     * @param e
     * @return
     */
    public Object defaultFallback(Throwable e) {
        LOGGER.info("defaultFallback method is executing:{}", e);
        return null;
    }

}
