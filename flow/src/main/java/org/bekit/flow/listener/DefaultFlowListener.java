/*
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:15 创建
 */
package org.bekit.flow.listener;

import org.bekit.event.EventPublisher;
import org.bekit.event.annotation.Listen;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.bekit.flow.annotation.listener.FlowListener;
import org.bekit.flow.event.DecidedNodeEvent;
import org.bekit.flow.event.DecidedStateNodeEvent;
import org.bekit.flow.event.FlowExceptionEvent;
import org.springframework.context.annotation.DependsOn;

/**
 * 默认的流程监听器
 * <p>
 * 监听所有流程发生的事件，然后将事件转发给对应流程的特定流程监听器（@TheFlowListener）
 */
@FlowListener
@DependsOn("org.bekit.event.bus.EventBusesHolder")      // 保证出现循环引用时不会出错
public class DefaultFlowListener {
    // 特定流程事件发布器
    private final EventPublisher eventPublisher;

    public DefaultFlowListener(EventBusesHolder eventBusesHolder) {
        eventPublisher = new DefaultEventPublisher(eventBusesHolder.getEventBus(TheFlowListenerType.class));
    }

    // 监听节点选择事件
    @Listen
    public void listenNodeDecidedEvent(DecidedNodeEvent event) {
        eventPublisher.publish(event);
    }

    // 监听状态节点选择事件
    @Listen
    public void listenStateNodeDecidedEvent(DecidedStateNodeEvent event) {
        eventPublisher.publish(event);
    }

    // 监听流程异常事件
    @Listen
    public void listenFlowExceptionEvent(FlowExceptionEvent event) {
        eventPublisher.publish(event);
    }
}
