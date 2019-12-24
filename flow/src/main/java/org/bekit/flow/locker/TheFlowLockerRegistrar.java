/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 19:19 创建
 */
package org.bekit.flow.locker;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 特定流程加锁器注册器
 */
public class TheFlowLockerRegistrar {
    // 特定流程加锁器执行器Map（key：加锁的流程名称）
    private final Map<String, TheFlowLockerExecutor> registrar = new ConcurrentHashMap<>();

    /**
     * 注册特定流程加锁器
     *
     * @param theFlowLockerExecutor 特定流程加锁器执行器
     * @return 被替换的特定流程加锁器执行器
     */
    public TheFlowLockerExecutor registerTheFlowLocker(TheFlowLockerExecutor theFlowLockerExecutor) {
        return registrar.put(theFlowLockerExecutor.getFlow(), theFlowLockerExecutor);
    }

    /**
     * 获取所有加锁的流程名称
     *
     * @return 所有加锁的流程名称
     */
    public Set<String> getFlows() {
        return Collections.unmodifiableSet(registrar.keySet());
    }

    /**
     * 获取特定流程加锁器
     *
     * @param flow 加锁的流程名称
     * @return 特定流程加锁器执行器
     */
    public TheFlowLockerExecutor getTheFlowLocker(String flow) {
        return registrar.get(flow);
    }
}
