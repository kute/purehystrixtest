package com.kute.hystrix.controller.base;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * created by bailong001 on 2018/10/03 10:47
 */
public class BaseController {

    protected <T> List<T> splitToList(String dotStr, Function<String, T> function) {
        return splitToList(dotStr, ",", function);
    }

    protected <T> List<T> splitToList(String dotStr, String separator, Function<String, T> function) {
        return Lists.transform(Splitter.on(separator).omitEmptyStrings().trimResults().splitToList(dotStr), function);
    }

}
