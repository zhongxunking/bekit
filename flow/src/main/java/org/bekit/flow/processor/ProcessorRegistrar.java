/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 14:41 创建
 */
package org.bekit.flow.processor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器注册器
 */
public class ProcessorRegistrar {
    // 注册器（key：处理器的名称）
    private final Map<String, ProcessorExecutor> registrar = new ConcurrentHashMap<>();

    /**
     * 注册处理器
     *
     * @param processorExecutor 处理器执行器
     * @return 被替换的处理器
     */
    public ProcessorExecutor registerProcessor(ProcessorExecutor processorExecutor) {
        return registrar.put(processorExecutor.getProcessorName(), processorExecutor);
    }

    /**
     * 获取所有处理器名称
     *
     * @return 所有处理器名称
     */
    public Set<String> getProcessorNames() {
        return Collections.unmodifiableSet(registrar.keySet());
    }

    /**
     * 获取处理器
     *
     * @param processor 处理器名称
     * @return 处理器执行器
     */
    public ProcessorExecutor getProcessor(String processor) {
        return registrar.get(processor);
    }
}
