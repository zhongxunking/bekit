/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.publish;

import org.apache.commons.lang3.exception.ExceptionUtils;
import top.bekit.event.EventPublisher;
import top.bekit.event.bus.EventBus;

/**
 * 事件发布器默认实现类
 */
public class DefaultEventPublisher implements EventPublisher {
    // 事件总线
    private EventBus eventBus;

    public DefaultEventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(Object event) {
        try {
            if (eventBus != null) {
                eventBus.dispatche(event);
            }
        } catch (Throwable e) {
            // 非运行时异常包装成UndeclaredThrowableException异常，让外部不用每次调用时都需要catch
            ExceptionUtils.wrapAndThrow(e);
        }
    }
}
