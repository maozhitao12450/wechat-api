package com.sfygroup.wechat.util.sfyutils.listener;

import java.io.File;
import java.util.Map;

public interface ReloadListenr {
    /**
     * 配置文件更新后会调用该方法
     * @param file 更新的配置文件
     * @param loadProperties 该文件中加载的配置
     * @param properties 所有的已知配置
     */
    void afterPropertyLoaded(File file, Map loadProperties, Map<String, Object> properties);
}
