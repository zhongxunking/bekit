/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.publisher;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bekit.event.EventPublisher;
import org.bekit.event.bus.EventBus;

/**
 * 事件发布器的默认实现
 */
@AllArgsConstructor
public class DefaultEventPublisher implements EventPublisher {
    // 事件总线
    private final EventBus eventBus;

    @Override
    public void publish(Object event) {
        try {
            eventBus.dispatch(event);
        } catch (Throwable e) {
            ExceptionUtils.rethrow(e);
        }
    }
}
