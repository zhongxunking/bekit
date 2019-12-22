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
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Node {
    /**
     * 节点名称
     */
    String name();

    /**
     * 是否自动执行
     */
    boolean autoExecute();

    /**
     * 是否有状态
     */
    boolean haveState();

    /**
     * 处理器
     */
    String processor();
}
