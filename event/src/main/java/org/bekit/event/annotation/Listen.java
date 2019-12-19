/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 12:53 创建
 */
package org.bekit.event.annotation;

import org.bekit.event.extension.support.ClassListenResolver;
import org.bekit.event.listener.PriorityType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 监听
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.bekit.event.annotation.listener.Listen(resolver = ClassListenResolver.class, priorityType = PriorityType.ASC)
public @interface Listen {
    /**
     * 优先级类型
     */
    @AliasFor(annotation = org.bekit.event.annotation.listener.Listen.class, attribute = "priorityType")
    PriorityType priorityType() default PriorityType.ASC;
}
