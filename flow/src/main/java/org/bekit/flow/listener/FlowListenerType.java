/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 19:46 创建
 */
package org.bekit.flow.listener;

import org.bekit.event.extension.EventTypeResolver;
import org.bekit.event.extension.ListenerType;
import org.bekit.event.extension.support.DefaultEventTypeResolver;

/**
 * 流程监听器类型
 */
public class FlowListenerType implements ListenerType {

    @Override
    public EventTypeResolver getResolver() {
        return DefaultEventTypeResolver.INSTANCE;
    }
}
