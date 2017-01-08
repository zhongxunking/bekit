/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-14 21:27 创建
 */
package top.bekit.flow.annotation.flow;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 流程
 * <p>
 * 流程从StartNode节点开始，执行到EndNode节点或WaitNode节点
 * 一：开启了流程事务情况
 * 1、在流程执行前会开启一个新事务并锁住目标对象
 * 2、如果是StateNode类型节点执行完成后，则会提交事务，然后会再次开启新事务并锁住目标对象
 * 3、如果是ProcessNode类型节点执行完成后，不会提交事务，会直接跳转到下个流程节点
 * 4、在流程结束前会提交事务
 * 5、如果执行过程中有任何异常抛到了流程引擎，则会回滚当前的事务，当然如果前面有StateNode类型节点已经提交了的事务是不会回滚的
 * <p>
 * 二：未开启流程事务情况
 * 1、整个执行过程中流程引擎是不会开启事务和锁目标对象
 * 2、StateNode类型节点和ProcessNode类型节点没有区别
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Flow {

    /**
     * 流程名称（默认使用被注解的类名，首字母小写）
     */
    String name() default "";

    /**
     * 是否开启流程事务（默认开启）
     */
    boolean enableFlowTx() default true;

}
