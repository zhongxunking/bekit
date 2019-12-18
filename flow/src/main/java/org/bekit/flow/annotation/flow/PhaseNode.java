/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:38 创建
 */
package org.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 阶段节点
 * <p>
 * 阶段节点是一个阶段开始的标志（需要新事务来执行），也是上一个阶段结束的标志（需要提交老事务），所以在阶段节点执行前会先提交事务然后开启新事务。
 * 对应的节点决策器返回值类型必须为String，入参类型可为：()、(TargetContext)、(T)、(T, TargetContext)————T表示能被对应的处理器返回结果赋值的类型。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Node(name = "", processor = "", autoExecute = true, newTx = true)
public @interface PhaseNode {
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
