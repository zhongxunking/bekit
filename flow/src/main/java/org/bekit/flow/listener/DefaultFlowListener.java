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
import org.bekit.event.bus.EventBusHub;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.bekit.flow.annotation.listener.FlowListener;
import org.bekit.flow.event.*;

/**
 * 默认的流程监听器
 * <p>
 * 监听所有流程发生的事件，然后将事件转发给对应流程的特定流程监听器（@TheFlowListener）
 */
@FlowListener
public class DefaultFlowListener {
    // 特定流程事件发布器
    private final EventPublisher eventPublisher;

    public DefaultFlowListener(EventBusHub eventBusHub) {
        eventPublisher = new DefaultEventPublisher(eventBusHub.getEventBus(TheFlowListenerType.class));
    }

    // 监听流程开始事件
    @Listen
    public void listenFlowStartEvent(FlowStartEvent event) {
        eventPublisher.publish(event);
    }

    // 监听正在执行的节点事件
    @Listen
    public void listenExecutingNodeEvent(ExecutingNodeEvent event) {
        eventPublisher.publish(event);
    }

    // 监听节点选择事件
    @Listen
    public void listenDecidedNodeEvent(DecidedNodeEvent event) {
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

    // 监听流程结束事件
    @Listen
    public void listenFlowEndEvent(FlowEndEvent event) {
        eventPublisher.publish(event);
    }
}
