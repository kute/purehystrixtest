package com.kute.hystrix.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created by bailong001 on 2018/10/03 17:15
 */
@Component
public class HystrixCatHandler implements BeanFactoryPostProcessor {

    private static String appkey = "com.sankuai.hotel.dealing";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new CatWrapHystrixConcurrencyStrategy());
    }

    static class CatWrapHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

        private Map<Long,Long> tIds = new ConcurrentHashMap<>();
//        Map<Long, Cat.Context> contextMap = new ConcurrentHashMap<>(512);

        @Override
        public <T> Callable<T> wrapCallable(final Callable<T> callable) {
            final long mainTId = Thread.currentThread().getId();
            if (!tIds.keySet().contains(mainTId)) {
                tIds.put(mainTId,mainTId);

//                Cat.Context context = new Cat.Context() {
//
//                    HashMap<String, String> map = new HashMap<>(8);
//                    @Override
//                    public void addProperty(String s, String s1) {
//                        map.put(s, s1);
//                    }
//
//                    @Override
//                    public String getProperty(String s) {
//                        return map.get(s);
//                    }
//                };
//                //替换成自己的app key
//                Cat.logRemoteCallClient(context, "server app key");
//                contextMap.put(mainTId, context);
            }

            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    tIds.remove(mainTId);
//                    Cat.logRemoteCallServer(contextMap.get(mainTId));
//                    try {
//                        T callResult = callable.call();
//                        return callResult;
//                    } finally {
//                        tIds.remove(mainTId);
//                        contextMap.remove(mainTId);
//                    }
                    return null;
                }
            };
        }
    }

    public static String getAppkey() {
        return appkey;
    }

    public static void setAppkey(String appkey) {
        HystrixCatHandler.appkey = appkey;
    }
}
