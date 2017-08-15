/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.annotation.listener;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 监听器
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Listener {

    /**
     * 类型
     */
    Class type();

    /**
     * 优先级
     * （具体执行顺序需要结合@Listen注解的priorityAsc属性共同决定）
     */
    int priority() default Integer.MAX_VALUE;

}
