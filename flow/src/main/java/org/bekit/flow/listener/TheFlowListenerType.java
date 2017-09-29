/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 20:16 创建
 */
package org.bekit.flow.listener;

import org.bekit.event.extension.EventTypeResolver;
import org.bekit.event.extension.ListenerType;
import org.bekit.flow.event.FlowExceptionEvent;
import org.bekit.flow.event.NodeDecidedEvent;

/**
 * 特定流程监听器类型
 */
public class TheFlowListenerType implements ListenerType {

    @Override
    public EventTypeResolver getResolver() {
        return TheFlowEventTypeResolver.INSTANCE;
    }

    // 特定流程事件类型解决器
    private static class TheFlowEventTypeResolver implements EventTypeResolver {
        /**
         * 实例
         */
        public static final TheFlowEventTypeResolver INSTANCE = new TheFlowEventTypeResolver();

        @Override
        public Object resolve(Object event) {
            if (event instanceof NodeDecidedEvent) {
                return new TheFlowEventType(((NodeDecidedEvent) event).getFlow(), TheFlowEventType.Type.NODE_DECIDED);
            }
            if (event instanceof FlowExceptionEvent) {
                return new TheFlowEventType(((FlowExceptionEvent) event).getFlow(), TheFlowEventType.Type.FLOW_EXCEPTION);
            }
            throw new IllegalArgumentException("无法识别的流程事件" + event);
        }
    }
}
