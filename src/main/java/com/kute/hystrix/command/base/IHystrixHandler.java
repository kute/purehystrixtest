package com.kute.hystrix.command.base;

import com.google.common.base.Preconditions;
import com.netflix.config.DynamicPropertyFactory;

/**
 * created by bailong001 on 2018/10/02 20:13
 *
 * https://github.com/Netflix/Hystrix/wiki/Configuration
 */
public interface IHystrixHandler {

    default <T> T getProperty(String propName, T defaultValue) {
        return getProperty(propName, defaultValue, null);
    }

    default <T> T getProperty(String propName, T defaultValue, final Runnable propertyChangeCallback) {

        Preconditions.checkNotNull(defaultValue);

        DynamicPropertyFactory instance = DynamicPropertyFactory.getInstance();
        System.out.println();
        switch (defaultValue.getClass().getSimpleName()) {
            case "String":
                return (T) instance.getStringProperty(propName, (String) defaultValue, propertyChangeCallback).get();
            case "Integer":
                return (T)Integer.valueOf(instance.getIntProperty(propName, (Integer) defaultValue, propertyChangeCallback).get());
            case "Float":
                return (T)Float.valueOf(instance.getFloatProperty(propName, (Float) defaultValue, propertyChangeCallback).get());
            case "Double":
                return (T)Double.valueOf(instance.getDoubleProperty(propName, (Double) defaultValue, propertyChangeCallback).get());
            case "Long":
                return (T)Long.valueOf(instance.getLongProperty(propName, (Long) defaultValue, propertyChangeCallback).get());
            case "Boolean":
                return (T)Boolean.valueOf(instance.getBooleanProperty(propName, (Boolean) defaultValue, propertyChangeCallback).get());
            default:
                return instance.getContextualProperty(propName, defaultValue, propertyChangeCallback).getValue();
        }
    }

}
