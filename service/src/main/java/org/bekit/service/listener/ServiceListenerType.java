/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 23:11 创建
 */
package org.bekit.service.listener;

import org.bekit.event.extension.EventTypeResolver;
import org.bekit.event.extension.ListenerType;
import org.bekit.event.extension.support.ClassEventTypeResolver;

/**
 * 服务监听器类型
 */
public class ServiceListenerType implements ListenerType {

    @Override
    public EventTypeResolver getResolver() {
        return ClassEventTypeResolver.INSTANCE;
    }
}
