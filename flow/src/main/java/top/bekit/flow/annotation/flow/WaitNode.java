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
 * 等待节点
 * （当等待节点不是第一个被执行的节点，则当流程跳转到等待节点时会被正常中断（等待节点还未被执行）；当等待节点是第一个被执行的节点，则这个等待节点的执行和状态节点没有任何区别————这个特性非常适合等待异步通知场景。
 * 对应的节点决策器返回值类型必须为String，入参类型可为：()、(TargetContext)、(T)、(T, TargetContext)————T表示能被对应的处理器返回结果赋值的类型）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Node(autoExecute = false, newTx = true)
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
