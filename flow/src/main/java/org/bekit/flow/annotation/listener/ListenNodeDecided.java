/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:06 创建
 */
package org.bekit.flow.annotation.listener;

import org.bekit.event.annotation.listener.Listen;
import org.bekit.flow.listener.ListenNodeDecidedResolver;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 监听节点选择事件
 * （配合TheFlowListener一起使用；流程执行过程中，当每次发生节点选择事件时都会调用注入本注解对应的方法；
 * 一个TheFlowListener内最多只能出现一次；对应的监听方法入参必须为（String, TargetContext））
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Listen(resolver = ListenNodeDecidedResolver.class)
public @interface ListenNodeDecided {
    /**
     * 是否按照优先级升序
     */
    @AliasFor(annotation = Listen.class, attribute = "priorityAsc")
    boolean priorityAsc() default true;
}
