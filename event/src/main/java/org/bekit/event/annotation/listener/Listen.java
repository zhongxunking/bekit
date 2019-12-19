/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.annotation.listener;

import org.bekit.event.extension.ListenResolver;
import org.bekit.event.listener.PriorityType;

import java.lang.annotation.*;

/**
 * 监听
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Listen {
    /**
     * 监听解决器
     */
    Class<? extends ListenResolver> resolver();

    /**
     * 优先级类型
     */
    PriorityType priorityType();
}
