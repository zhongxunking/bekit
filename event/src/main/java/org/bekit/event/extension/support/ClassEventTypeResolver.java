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
 * Class事件类型解决器（事件类型就是事件对应的Class类）
 */
public class ClassEventTypeResolver implements EventTypeResolver {
    /**
     * 实例
     */
    public static final ClassEventTypeResolver INSTANCE = new ClassEventTypeResolver();

    @Override
    public Object resolve(Object event) {
        return event.getClass();
    }
}
