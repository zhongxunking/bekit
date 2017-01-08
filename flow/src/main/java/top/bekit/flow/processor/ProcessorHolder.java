/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 14:41 创建
 */
package top.bekit.flow.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.bekit.flow.annotation.processor.Processor;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理器持有器（会被注册到spring容器中）
 */
public class ProcessorHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 处理器执行器Map（key：处理器的名称）
    private Map<String, ProcessorExecutor> processorExecutorMap = new HashMap<String, ProcessorExecutor>();

    // 初始化（查询spring容器中所有的@Processor处理器并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Processor.class);
        for (String beanName : beanNames) {
            // 解析处理器
            ProcessorExecutor processorExecutor = ProcessorParser.parseProcessor(applicationContext.getBean(beanName));
            if (processorExecutorMap.containsKey(processorExecutor.getProcessorName())) {
                throw new RuntimeException("存在重名的处理器：" + processorExecutor.getProcessorName());
            }
            // 将执行器放入持有器中
            processorExecutorMap.put(processorExecutor.getProcessorName(), processorExecutor);
        }
    }

    /**
     * 获取处理器执行器
     *
     * @param processor 处理器名称
     * @throws RuntimeException 如果不存在该处理器执行器
     */
    public ProcessorExecutor getRequiredProcessorExecutor(String processor) {
        if (!processorExecutorMap.containsKey(processor)) {
            throw new RuntimeException("不存在处理器：" + processor);
        }
        return processorExecutorMap.get(processor);
    }
}
