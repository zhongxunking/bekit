/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.annotation.listener;

import org.bekit.event.extension.ListenerType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 监听器
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Listener {
    /**
     * 类型
     */
    Class<? extends ListenerType> type();

    /**
     * 优先级
     */
    int priority();
}
