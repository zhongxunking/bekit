/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.annotation;

import org.bekit.event.extension.support.ListenerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 监听器
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@org.bekit.event.annotation.listener.Listener(type = ListenerType.class, priority = Integer.MAX_VALUE)
public @interface Listener {
    /**
     * 优先级
     */
    @AliasFor(annotation = org.bekit.event.annotation.listener.Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;
}
