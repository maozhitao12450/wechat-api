package com.sfygroup.wechat.util.sfyutils.interfaces;

import java.util.Map;

/**
 * 配置文件重加载后执行该接口方法,
 * 注意：实现该接口必须重写其中之一方法，不然会导致堆栈溢出
 *
 * @author 毛志涛
 */
public interface PropertiesReloadAble {

    /**
     * 配置文件重加载后执行该接口方法
     *
     * @param property
     */
    default void afterReloadProperties(Map<String, Object> property) {
        afterLoadProperties(property);
    }

    /**
     * 配置文件加载后执行该方法
     *
     * @param property
     */
    default void afterLoadProperties(Map<String, Object> property) {
        afterReloadProperties(property);
    }
}
