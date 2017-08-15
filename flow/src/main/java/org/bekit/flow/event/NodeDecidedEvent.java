/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:16 创建
 */
package org.bekit.flow.event;

import org.bekit.flow.engine.TargetContext;

/**
 * 节点选择事件
 */
public class NodeDecidedEvent {
    // 流程名称
    private String flow;
    // 被选择的节点
    private String node;
    // 目标上下文
    private TargetContext targetContext;

    public NodeDecidedEvent(String flow, String node, TargetContext targetContext) {
        this.flow = flow;
        this.node = node;
        this.targetContext = targetContext;
    }

    /**
     * 获取流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 获取被选择的节点
     */
    public String getNode() {
        return node;
    }

    /**
     * 获取目标上下文
     */
    public TargetContext getTargetContext() {
        return targetContext;
    }
}
