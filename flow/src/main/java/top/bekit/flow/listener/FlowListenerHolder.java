/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 23:27 创建
 */
package top.bekit.flow.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.bekit.flow.annotation.listener.FlowListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程监听器持有器（会被注册到spring容器中）
 */
public class FlowListenerHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 流程监听器执行器Map（key：被监听的流程名称）
    private Map<String, List<FlowListenerExecutor>> flowListenerExecutorsMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@FlowListener流程监听器并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(FlowListener.class);
        for (String beanName : beanNames) {
            // 解析流程监听器
            FlowListenerExecutor flowListenerExecutor = FlowListenerParser.parseFlowListener(applicationContext.getBean(beanName));
            // 将执行器放入持有器中
            List<FlowListenerExecutor> flowListenerExecutors = flowListenerExecutorsMap.get(flowListenerExecutor.getFlow());
            if (flowListenerExecutors == null) {
                flowListenerExecutors = new ArrayList<>();
            }
            flowListenerExecutors.add(flowListenerExecutor);
            flowListenerExecutorsMap.put(flowListenerExecutor.getFlow(), flowListenerExecutors);
        }
    }

    /**
     * 获取流程监听器执行器
     *
     * @param flow 被监听的流程名称
     * @return 如果不存在该流程的监听器，则返回空List
     */
    public List<FlowListenerExecutor> getFlowListenerExecutors(String flow) {
        if (!flowListenerExecutorsMap.containsKey(flow)) {
            return new ArrayList<>();
        }
        return flowListenerExecutorsMap.get(flow);
    }
}
