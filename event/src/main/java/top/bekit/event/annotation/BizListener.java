/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.annotation;

import org.springframework.core.annotation.AliasFor;
import top.bekit.event.annotation.listener.Listener;

import java.lang.annotation.*;

/**
 * 业务监听器
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Listener(type = BizListener.class)
public @interface BizListener {

    /**
     * 优先级（越小优先级越高）
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;

}
