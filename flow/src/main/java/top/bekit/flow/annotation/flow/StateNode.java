/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:38 创建
 */
package top.bekit.flow.annotation.flow;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 状态节点
 * （对于开启了流程事务情况，此类型节点处理完成后会提交事务，然后会再次开启新事务并锁住目标对象；
 * 对应的节点决策器返回值类型必须为String，入参类型可为：()、(TargetContext)、(T)、(T, TargetContext)————T表示能被对应的处理器返回结果赋值的类型）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Node(newTx = true)
public @interface StateNode {

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
