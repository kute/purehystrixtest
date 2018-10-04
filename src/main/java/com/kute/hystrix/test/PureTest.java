package com.kute.hystrix.test;

import com.kute.hystrix.command.ExceptionCommand;
import com.kute.hystrix.command.GetUserCollapser;
import com.kute.hystrix.command.GetUserCommand;
import com.kute.hystrix.command.SleepCommand;
import com.kute.hystrix.domain.UserData;
import com.kute.hystrix.service.PureService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import jdk.management.resource.internal.FutureWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * created by bailong001 on 2018/09/30 15:35
 */
public class PureTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PureTest.class);

    private PureService pureService = new PureService();

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 同步执行，超时降级:execute()
     */
    @Test
    public void test1() {

        SleepCommand command = new SleepCommand(pureService, 1000 * 10L);

        command.execute();
    }

    /**
     * 异步执行：queue()
     * <p>
     * execute() == queue().get()
     *
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Test
    public void test2() throws InterruptedException, ExecutionException, TimeoutException {

        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try{

            GetUserCommand command = new GetUserCommand(pureService, 22L);

            Future<UserData> future = command.queue();

            System.out.println(future.get(30, TimeUnit.SECONDS));

        } finally{
            context.shutdown();
        }


    }

    /**
     * 验证 HystrixBadRequestException 不会触发 降级逻辑
     */
    @Test
    public void test3() {
        TimeoutException timeoutException = new TimeoutException("timeout");

        ExceptionCommand command = new ExceptionCommand(timeoutException);

        command.execute();

        command = new ExceptionCommand(new HystrixBadRequestException("Illegal param exception"));

        try {
            command.execute();
        } catch (Exception e) {
            Assert.assertTrue(e.getClass().getSimpleName().equals("HystrixBadRequestException"));
        }
    }

    /**
     * 请求缓存 验证
     * 重复执行 命令
     */
    @Test
    public void test4() {

        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            Long id = 88L;

            GetUserCommand commandA = new GetUserCommand(pureService, id);

            UserData dataA = commandA.execute();

            System.out.println(dataA);

            Assert.assertFalse(commandA.isResponseFromCache());

            GetUserCommand commandB = new GetUserCommand(pureService, id);

            UserData dataB = commandB.execute();

            System.out.println(dataB);

            Assert.assertTrue(dataA.getName().equals(dataB.getName()));

            Assert.assertTrue(commandB.isResponseFromCache());

        } finally {
            context.shutdown();
        }

    }

}
