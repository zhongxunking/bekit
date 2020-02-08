/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.annotation.listener;

import org.bekit.event.annotation.listener.Listener;
import org.bekit.service.listener.ServiceListenerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 服务监听器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Listener(type = ServiceListenerType.class, priority = Integer.MAX_VALUE)
public @interface ServiceListener {
    /**
     * 优先级
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;
}
