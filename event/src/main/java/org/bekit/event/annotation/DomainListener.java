/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.annotation;

import org.bekit.event.annotation.listener.Listener;
import org.bekit.event.extension.support.DomainListenerType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 领域监听器
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Listener(type = DomainListenerType.class, priority = Integer.MAX_VALUE)
public @interface DomainListener {
    /**
     * 优先级
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;
}
