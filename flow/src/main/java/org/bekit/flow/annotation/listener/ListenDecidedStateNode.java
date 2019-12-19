/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:06 创建
 */
package org.bekit.flow.annotation.listener;

import org.bekit.event.annotation.listener.Listen;
import org.bekit.event.listener.PriorityType;
import org.bekit.flow.listener.ListenNodeDecidedResolver;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 监听被选择的状态节点
 * <p>
 * 配合@TheFlowListener一起使用；当每次节点决策器选择下一个状态节点后，都会调用本注解标注的方法。
 * 对应的方法入参类型必须为（String, FlowContext）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Listen(resolver = ListenNodeDecidedResolver.class, priorityType = PriorityType.ASC)
public @interface ListenDecidedStateNode {
    /**
     * 优先级类型（默认为升序）
     */
    @AliasFor(annotation = Listen.class, attribute = "priorityType")
    PriorityType priorityType() default PriorityType.ASC;
}
