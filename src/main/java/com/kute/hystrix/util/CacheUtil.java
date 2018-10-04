package com.kute.hystrix.util;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.cache.*;
import com.google.common.primitives.Longs;
import com.kute.hystrix.domain.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kute on 2017/12/10.
 */
public class CacheUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtil.class);

    /**
     * 当元素过期被移除时 接受通知,注意此监听器是 在移除时 同步调用的
     */
    private static final RemovalListener<String, UserData> removalListener = removalNotification -> LOGGER.info("the data [{}] is being removed and i get it.", removalNotification.getValue());

    /**
     * 本地缓存,降级使用
     */
    private static final LoadingCache<String, UserData> LOCAL_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // 当缓存中项被移除时触发 通知
            .removalListener(removalListener)
            .build(new CacheLoader<String, UserData>() {
                @Override
                public UserData load(String key) throws Exception {
                    Long id = parseUserCacheKey(key);
                    LOGGER.info("get from local cache:key={}, id={}", key, id);
                    return UserData.randUser(id);
                }
            });

    public static UserData getFromLocalCache(long id) {
        String key = userCacheKey(id);
        try {
            return LOCAL_CACHE.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String userCacheKey(long id) {
        return Joiner.on("_").join("cache_user_", id);
    }

    private static Long parseUserCacheKey(String key) {
        Preconditions.checkNotNull(key);
        Preconditions.checkArgument(key.contains("_"));
        List<String> pieceList = Splitter.on("_").omitEmptyStrings().trimResults().splitToList(key);
        return Longs.tryParse(pieceList.get(pieceList.size() - 1));
    }
}
