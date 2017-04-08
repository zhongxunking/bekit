/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-15 23:41 创建
 */
package top.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 开始节点
 * （流程是从这个节点开始）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Node(commitTx = true)
public @interface StartNode {

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
