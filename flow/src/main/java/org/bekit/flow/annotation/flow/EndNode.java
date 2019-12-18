/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 结束节点
 * <p>
 * 结束节点是流程结束的标志。当流程跳转到结束节点时，流程会自动结束。
 * 对应的节点决策器返回类型必须是void，且不能有入参。节点决策器的方法体不会被执行。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Node(name = "", processor = "", autoExecute = false, newTx = true)
public @interface EndNode {
    /**
     * 节点名称（默认使用被注解的函数名）
     */
    @AliasFor(annotation = Node.class, attribute = "name")
    String name() default "";
}
