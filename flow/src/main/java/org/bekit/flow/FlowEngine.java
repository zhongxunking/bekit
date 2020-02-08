/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 19:06 创建
 */
package org.bekit.flow;

import java.util.Map;

/**
 * 流程引擎
 */
public interface FlowEngine {
    /**
     * 执行流程
     *
     * @param flow   流程名称
     * @param target 目标对象
     * @return 流程执行结束后的目标对象（可能和传入的目标对象不是同一个对象）
     */
    <T> T execute(String flow, T target);

    /**
     * 执行流程
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件
     * @return 流程执行结束后的目标对象（可能和传入的目标对象不是同一个对象）
     */
    <T> T execute(String flow, T target, Map<Object, Object> attachment);
}
