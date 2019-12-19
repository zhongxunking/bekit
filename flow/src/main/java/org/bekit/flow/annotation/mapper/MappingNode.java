/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-28 20:59 创建
 */
package org.bekit.flow.annotation.mapper;

import java.lang.annotation.*;

/**
 * 映射出节点
 * <p>
 * 流程刚开始执行以及流程加锁器每次加锁后都会调用映射出节点，映射出接下来要执行的节点。
 * 被标记的方法入参为FlowContext，返回类型值为要执行的节点
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingNode {
}
