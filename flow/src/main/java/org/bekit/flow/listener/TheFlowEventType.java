/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 19:53 创建
 */
package org.bekit.flow.listener;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 特定流程事件类型
 */
@AllArgsConstructor
public class TheFlowEventType {
    // 流程
    private final String flow;
    // 类型
    private final Class eventClass;

    @Override
    public int hashCode() {
        return Objects.hash(flow, eventClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TheFlowEventType)) {
            return false;
        }
        TheFlowEventType other = (TheFlowEventType) obj;
        return StringUtils.equals(flow, other.flow) && eventClass == other.eventClass;
    }

    @Override
    public String toString() {
        return String.format("TheFlowEventType{flow=\"%s\",eventClass=%s}", flow, eventClass);
    }
}
