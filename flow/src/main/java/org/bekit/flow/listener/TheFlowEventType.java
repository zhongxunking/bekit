/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 19:53 创建
 */
package org.bekit.flow.listener;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 特定流程事件类型
 */
public class TheFlowEventType {
    // 流程
    private String flow;
    // 类型
    private Type type;

    public TheFlowEventType(String flow, Type type) {
        this.flow = flow;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(flow, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TheFlowEventType)) {
            return false;
        }
        TheFlowEventType other = (TheFlowEventType) obj;
        return StringUtils.equals(flow, other.flow) && type == other.type;
    }

    /**
     * 类型
     */
    public enum Type {
        // 节点被选择
        NODE_DECIDED,
        // 流程异常
        FLOW_EXCEPTION;
    }
}
