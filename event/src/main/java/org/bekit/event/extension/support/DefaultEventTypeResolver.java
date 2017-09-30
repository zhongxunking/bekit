/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 11:51 创建
 */
package org.bekit.event.extension.support;

import org.bekit.event.extension.EventTypeResolver;

/**
 * 默认的事件类型解决器实现（事件类型就是事件对应的Class类）
 */
public class DefaultEventTypeResolver implements EventTypeResolver {
    /**
     * 实例
     */
    public static final DefaultEventTypeResolver INSTANCE = new DefaultEventTypeResolver();

    @Override
    public Object resolve(Object event) {
        return event.getClass();
    }
}
