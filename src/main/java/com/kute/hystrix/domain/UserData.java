package com.kute.hystrix.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * created by bailong001 on 2018/09/30 14:18
 */
@Setter
@Getter
@ToString
public class UserData implements Serializable {

    private Long id;

    private String name;

    private Date birthday;

    public UserData() {
    }

    public UserData(Long id, String name, Date birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public static UserData randUser() {
        return randUser(RandomUtils.nextLong(10, 20));
    }

    /**
     * 降级使用
     * @return
     */
    public static UserData randCacheUser() {
        return new UserData(RandomUtils.nextLong(10, 20), "cache_" + RandomStringUtils.randomAlphabetic(4), new Date());
    }

    public static UserData randCacheUser(long id) {
        return new UserData(id, "cache_" + RandomStringUtils.randomAlphabetic(4), new Date());
    }

    public static UserData randUser(long id) {
        return new UserData(id, RandomStringUtils.randomAlphabetic(4), new Date());
    }
}
