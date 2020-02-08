/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-14 21:27 创建
 */
package org.bekit.flow.annotation.flow;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 流程
 * <p>
 * 流程包含的节点类型：开始节点（@StartNode）、阶段节点（@PhaseNode）、瞬态节点（@TransientNode）、暂停节点（@PauseNode）、结束节点（@EndNode）
 * 执行步骤：
 * 1、节点初始化为开始节点（@StartNode）
 * 2、调用流程映射器映射出要真正执行的节点（如果存在）
 * 3、调用流程加锁器流程加锁（如果存在，并调用流程映射器映射出要真正执行的节点（如果存在））
 * 4、开启事务
 * 5、调用流程加锁器状态加锁（如果存在，并调用流程映射器映射出要真正执行的节点（如果存在））
 * 6、执行节点并得到下一个节点
 * 7、下一个节点是瞬态节点（@TransientNode）则进入步骤6；否则进入步骤8
 * 8、调用流程加锁器状态解锁（如果存在）
 * 9、提交事务
 * 10、下一个节点是暂停节点（@PauseNode）或结束节点（@EndNode），则进入步骤11；否则进入步骤4
 * 11、调用流程加锁器流程解锁（如果存在）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Flow {
    /**
     * 流程名称（默认使用被注解的类名且首字母小写）
     */
    String name() default "";
}
