/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-15 23:36 创建
 */
package org.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 暂停节点
 * <p>
 * 暂停节点是正常中断流程的节点。暂停节点只有是第一个被执行的节点情况下，暂停节点才会被执行。
 * 对应的节点决策器返回值类型必须为String，入参类型可为：()、(FlowContext)、(T)、(T, FlowContext)————T表示能被对应的处理器返回结果赋值的类型。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Node(name = "", processor = "", autoExecute = false, haveState = true)
public @interface PauseNode {
    /**
     * 节点名称（默认使用被注解的函数名）
     */
    @AliasFor(annotation = Node.class, attribute = "name")
    String name() default "";

    /**
     * 节点处理器（默认不执行处理器）
     */
    @AliasFor(annotation = Node.class, attribute = "processor")
    String processor() default "";
}
