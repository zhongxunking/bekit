/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:45 创建
 */
package org.bekit.flow.annotation.listener;

import org.bekit.event.annotation.listener.Listener;
import org.bekit.flow.listener.FlowListenerType;
import org.bekit.flow.listener.TheFlowListenerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 流程监听器
 * （监听的是所有流程发生的事件）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Listener(type = TheFlowListenerType.class)
public @interface FlowListener {
    /**
     * 优先级
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;
}
