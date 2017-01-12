/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:06 创建
 */
package top.bekit.flow.annotation.listener;

import java.lang.annotation.*;

/**
 * 监听节点选择事件
 * （监听节点选择事件主要是用来更新目标对象的状态，实现更新目标对象状态和流程定义分离）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenNodeDecide {

    /**
     * 匹配被选择节点名称的表达式（正则表达式）
     */
    String nodeExpression();

}
