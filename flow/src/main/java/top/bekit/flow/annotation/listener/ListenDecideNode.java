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
 * 监听选择节点事件
 * （监听选择节点事件主要是用来更新节点状态到目标对象，实现更新目标对象状态和定义流程那部分代码分离）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenDecideNode {

    /**
     * 匹配被选择节点的表达式（正则表达式）
     */
    String nodeExpression();

}
