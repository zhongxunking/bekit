/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:34 创建
 */
package org.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 瞬态节点
 * <p>
 * 瞬态节点是一种单纯的处理单元，执行前不会提交和新建事务。
 * 对应的节点决策器返回值类型必须为String，入参类型可为：()、(FlowContext)、(T)、(T, FlowContext)————T表示能被对应的处理器返回结果赋值的类型。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Node(name = "", autoExecute = true, haveState = false, processor = "")
public @interface TransientNode {
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
