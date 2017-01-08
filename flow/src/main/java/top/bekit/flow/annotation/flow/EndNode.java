/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 结束节点
 * 1、流程执行到结束节点自动结束，如果开启了流程事务会自动提交事务
 * 2、对应的流程方法不能有入参，且返回类型必须是void，方法体不会被执行（比如：void end(){}）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Node(autoExecute = false, commitTransaction = true)
public @interface EndNode {

    /**
     * 节点名称（默认使用被注解的函数名，在一个流程图内节点名称需唯一）
     */
    @AliasFor(annotation = Node.class, attribute = "name")
    String name() default "";

}
