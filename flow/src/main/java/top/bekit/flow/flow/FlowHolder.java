/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:26 创建
 */
package top.bekit.flow.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.bekit.event.bus.EventBusHolder;
import top.bekit.flow.annotation.flow.Flow;
import top.bekit.flow.processor.ProcessorHolder;
import top.bekit.flow.transaction.FlowTxHolder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程持有器（会被注册到spring容器中）
 */
public class FlowHolder {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ProcessorHolder processorHolder;
    @Autowired
    private FlowTxHolder flowTxHolder;
    @Autowired
    private EventBusHolder eventBusHolder;
    // 流程执行器Map（key：流程名称）
    private Map<String, FlowExecutor> flowExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@Flow流程并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Flow.class);
        for (String beanName : beanNames) {
            // 解析流程
            FlowExecutor flowExecutor = FlowParser.parseFlow(applicationContext.getBean(beanName), processorHolder, flowTxHolder, eventBusHolder);
            if (flowExecutorMap.containsKey(flowExecutor.getFlowName())) {
                throw new RuntimeException("存在重名的流程" + flowExecutor.getFlowName());
            }
            // 将执行器放入持有器中
            flowExecutorMap.put(flowExecutor.getFlowName(), flowExecutor);
        }
    }

    /**
     * 获取流程执行器
     *
     * @param flow 流程名称
     * @throws RuntimeException 如果不存在该流程执行器
     */
    public FlowExecutor getRequiredFlowExecutor(String flow) {
        if (!flowExecutorMap.containsKey(flow)) {
            throw new RuntimeException("不存在流程" + flow);
        }
        return flowExecutorMap.get(flow);
    }

}
