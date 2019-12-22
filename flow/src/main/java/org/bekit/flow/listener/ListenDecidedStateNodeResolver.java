/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 20:29 创建
 */
package org.bekit.flow.listener;

import org.bekit.event.extension.ListenResolver;
import org.bekit.flow.annotation.listener.TheFlowListener;
import org.bekit.flow.engine.FlowContext;
import org.bekit.flow.event.DecidedNodeEvent;
import org.bekit.flow.event.DecidedStateNodeEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * 监听注解@ListenDecidedNode的解决器
 */
public class ListenDecidedStateNodeResolver implements ListenResolver {
    // 监听的事件类型
    private TheFlowEventType eventType;

    @Override
    public void init(Method listenMethod) {
        TheFlowListener theFlowListenerAnnotation = AnnotatedElementUtils.findMergedAnnotation(listenMethod.getDeclaringClass(), TheFlowListener.class);
        if (theFlowListenerAnnotation == null) {
            throw new IllegalArgumentException("@ListenDecidedStateNode只能标注在特定流程监听器（@TheFlowListener）的方法上");
        }
        // 校验入参类型
        Class[] parameterTypes = listenMethod.getParameterTypes();
        Assert.isTrue(parameterTypes.length == 2
                && parameterTypes[0] == String.class
                && parameterTypes[1] == FlowContext.class, String.format("@ListenDecidedStateNode方法[%s]的入参类型必须是(String, FlowContext<T>)", listenMethod));

        eventType = new TheFlowEventType(theFlowListenerAnnotation.flow(), DecidedStateNodeEvent.class);
    }

    @Override
    public Object getEventType() {
        return eventType;
    }

    @Override
    public Object[] resolveArgs(Object event) {
        DecidedStateNodeEvent decidedStateNodeEvent = (DecidedStateNodeEvent) event;
        return new Object[]{decidedStateNodeEvent.getStateNode(), decidedStateNodeEvent.getContext()};
    }
}
