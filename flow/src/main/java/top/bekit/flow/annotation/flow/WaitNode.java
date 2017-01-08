/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-15 23:36 创建
 */
package top.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 等待节点（等待异步通知场景可以使用此节点）
 * <p>
 * 如果流程下一个要执行的节点是等待类型，则会自动停止流程；
 * 如果需要执行等待类型节点，则需要手动触发从等待类型节点开始执行
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Node(autoExecute = false, commitTransaction = true)
public @interface WaitNode {

    /**
     * 节点名称（默认使用被注解的函数名，在一个流程图内节点名称需唯一）
     */
    @AliasFor(annotation = Node.class, attribute = "name")
    String name() default "";

    /**
     * 节点处理器（默认不执行处理器）
     */
    @AliasFor(annotation = Node.class, attribute = "processor")
    String processor() default "";

}
