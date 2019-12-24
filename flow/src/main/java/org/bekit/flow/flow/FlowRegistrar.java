/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:26 创建
 */
package org.bekit.flow.flow;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程注册器
 */
public class FlowRegistrar {
    // 注册器
    private final Map<String, FlowExecutor> registrar = new ConcurrentHashMap<>();

    /**
     * 注册流程
     *
     * @param flowExecutor 流程执行器
     * @return 被替换的流程执行器
     */
    public FlowExecutor registerFlow(FlowExecutor flowExecutor) {
        return registrar.put(flowExecutor.getFlowName(), flowExecutor);
    }

    /**
     * 获取所有流程名称
     *
     * @return 所有流程名称
     */
    public Set<String> getFlowNames() {
        return Collections.unmodifiableSet(registrar.keySet());
    }

    /**
     * 获取流程
     *
     * @param flow 流程名称
     * @return 流程执行器
     */
    public FlowExecutor getFlow(String flow) {
        return registrar.get(flow);
    }
}
