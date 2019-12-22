/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 14:41 创建
 */
package org.bekit.flow.processor;

import org.bekit.flow.annotation.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 处理器持有器
 */
@Component
public class ProcessorsHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 处理器执行器Map（key：处理器的名称）
    private final Map<String, ProcessorExecutor> processorExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@Processor处理器并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Processor.class);
        for (String beanName : beanNames) {
            // 解析处理器
            ProcessorExecutor processorExecutor = ProcessorParser.parseProcessor(applicationContext.getBean(beanName));
            Assert.isTrue(!processorExecutorMap.containsKey(processorExecutor.getProcessorName()), String.format("存在重名的处理器[%s]", processorExecutor.getProcessorName()));
            // 将执行器放入持有器中
            processorExecutorMap.put(processorExecutor.getProcessorName(), processorExecutor);
        }
    }

    /**
     * 获取所有处理器名称
     */
    public Set<String> getProcessorNames() {
        return Collections.unmodifiableSet(processorExecutorMap.keySet());
    }

    /**
     * 获取处理器执行器
     *
     * @param processor 处理器名称
     * @throws IllegalArgumentException 如果不存在该处理器执行器
     */
    public ProcessorExecutor getRequiredProcessorExecutor(String processor) {
        ProcessorExecutor processorExecutor = processorExecutorMap.get(processor);
        if (processorExecutor == null) {
            throw new IllegalArgumentException(String.format("不存在处理器[%s]", processor));
        }
        return processorExecutor;
    }
}
