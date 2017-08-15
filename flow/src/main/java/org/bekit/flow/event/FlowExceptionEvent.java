/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:21 创建
 */
package org.bekit.flow.event;

import org.bekit.flow.engine.TargetContext;

/**
 * 流程异常事件
 */
public class FlowExceptionEvent {
    // 流程名称
    private String flow;
    // 发生的异常
    private Throwable throwable;
    // 目标上下文
    private TargetContext targetContext;

    public FlowExceptionEvent(String flow, Throwable throwable, TargetContext targetContext) {
        this.flow = flow;
        this.throwable = throwable;
        this.targetContext = targetContext;
    }

    /**
     * 获取流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 获取发生的异常
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * 获取目标上下文
     */
    public TargetContext getTargetContext() {
        return targetContext;
    }
}
