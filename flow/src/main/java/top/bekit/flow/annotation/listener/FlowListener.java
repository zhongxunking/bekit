/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:45 创建
 */
package top.bekit.flow.annotation.listener;

import org.springframework.core.annotation.AliasFor;
import top.bekit.event.annotation.listener.Listener;

import java.lang.annotation.*;

/**
 * 流程监听器
 * （监听的是所有流程发生的事件）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Listener(type = FlowListener.class)
public @interface FlowListener {

    /**
     * 优先级
     * （具体执行顺序需要结合@Listen注解的priorityAsc属性共同决定）
     */
    @AliasFor(annotation = Listener.class, attribute = "priority")
    int priority() default Integer.MAX_VALUE;

}
