/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 16:19 创建
 */
package org.bekit.flow.annotation.listener;

import org.bekit.event.annotation.listener.Listener;
import org.bekit.flow.listener.TheFlowListenerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 特定流程监听器
 * <p>
 * 监听某一个特定流程发生的事件，配合@ListenDecidedNode、@ListenDecidedStateNode、@ListenFlowException一起使用。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Listener(type = TheFlowListenerType.class, priority = Integer.MAX_VALUE)
public @interface TheFlowListener {
    /**
     * 监听的流程
     */
    String flow();

    /**
     * 优先级
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;
}
