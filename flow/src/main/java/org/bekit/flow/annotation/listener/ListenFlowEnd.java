/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 18:41 创建
 */
package org.bekit.flow.annotation.listener;

import org.bekit.event.annotation.listener.Listen;
import org.bekit.event.listener.PriorityType;
import org.bekit.flow.listener.ListenFlowEndResolver;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 监听流程结束事件
 * <p>
 * 配合@TheFlowListener一起使用；当流程执行结束时，都会调用本注解标注的方法。
 * 对应的方法入参类型必须为（FlowContext）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Listen(resolver = ListenFlowEndResolver.class, priorityType = PriorityType.ASC)
public @interface ListenFlowEnd {
    /**
     * 优先级类型（默认为升序）
     */
    @AliasFor(annotation = Listen.class, attribute = "priorityType")
    PriorityType priorityType() default PriorityType.ASC;
}
