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
 * 2、调用流程加锁器加流程锁（如果存在，并调用流程映射器映射出要真正执行的节点（如果存在））
 * 3、开启事务
 * 4、调用流程加锁器加状态锁（如果存在，并调用流程映射器映射出要真正执行的节点（如果存在））
 * 5、执行节点并得到下一个节点
 * 6、下一个节点是瞬态节点（@TransientNode）则进入步骤5；否则进入步骤7
 * 7、调用流程加锁器解状态锁（如果存在）
 * 8、提交事务
 * 9、下一个节点是暂停节点（@PauseNode）或结束节点（@EndNode），则进入步骤10；否则进入步骤3
 * 10、调用流程加锁器解流程锁（如果存在）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Flow {
    /**
     * 流程名称（默认使用被注解的类名，首字母小写）
     */
    String name() default "";
}
