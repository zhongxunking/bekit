/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-14 21:44 创建
 */

package org.bekit.flow.annotation.processor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 处理器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Processor {
    /**
     * 处理器名字（默认使用被注解的类名且首字母小写）
     */
    String name() default "";
}
