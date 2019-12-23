/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 20:21 创建
 */
package org.bekit.flow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bekit.flow.engine.FlowContext;

/**
 * 流程开始事件
 */
@AllArgsConstructor
@Getter
public class FlowStartEvent {
    // 流程名称
    private final String flow;
    // 流程上下文
    private final FlowContext context;
}
