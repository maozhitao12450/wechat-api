package com.sfygroup.wechat.util.sfyutils.listener;

import java.util.Map;

/**
 * 配置文件更新监听器
 */
public interface PropertiesReloadListener {
    void propertiesFileReload(Map properties);
}
