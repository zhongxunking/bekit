/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 17:45 创建
 */
package org.bekit.flow.mapper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 特定流程映射器注册器
 */
public class TheFlowMapperRegistrar {
    // 注册器（key：映射的流程名称）
    private final Map<String, TheFlowMapperExecutor> registrar = new ConcurrentHashMap<>();

    /**
     * 注册特定流程映射器
     *
     * @param theFlowMapperExecutor 解析特定流程映射器执行器
     * @return 被替换的注册特定流程映射器
     */
    public TheFlowMapperExecutor registerTheFlowMapper(TheFlowMapperExecutor theFlowMapperExecutor) {
        return registrar.put(theFlowMapperExecutor.getFlow(), theFlowMapperExecutor);
    }

    /**
     * 获取所有映射的流程名称
     *
     * @return 所有映射的流程名称
     */
    public Set<String> getFlows() {
        return Collections.unmodifiableSet(registrar.keySet());
    }

    /**
     * 获取特定流程映射器
     *
     * @param flow 映射的流程名称
     * @return 特定流程映射器执行器
     */
    public TheFlowMapperExecutor getTheFlowMapper(String flow) {
        return registrar.get(flow);
    }
}
