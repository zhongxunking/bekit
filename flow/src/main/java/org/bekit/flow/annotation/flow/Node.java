/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-14 21:35 创建
 */
package org.bekit.flow.annotation.flow;

import java.lang.annotation.*;

/**
 * 节点
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Node {
    /**
     * 节点名称
     */
    String name();

    /**
     * 节点处理器
     */
    String processor();

    /**
     * 是否自动执行本节点
     */
    boolean autoExecute();

    /**
     * 本节点执行前是否创建新事务
     */
    boolean newTx();
}
