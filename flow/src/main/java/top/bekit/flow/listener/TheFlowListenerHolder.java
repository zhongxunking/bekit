/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 18:20 创建
 */
package top.bekit.flow.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import top.bekit.flow.annotation.listener.TheFlowListener;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 特定流程监听器持有器
 */
public class TheFlowListenerHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 特定流程监听器执行器Map（key：被监听的流程名称）
    private Map<String, TheFlowListenerExecutor> theFlowListenerExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@TheFlowListener特定流程监听器并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(TheFlowListener.class);
        for (String beanName : beanNames) {
            // 解析特定流程监听器
            TheFlowListenerExecutor theFlowListenerExecutor = TheFlowListenerParser.parseTheFlowListener(applicationContext.getBean(beanName));
            if (theFlowListenerExecutorMap.containsKey(theFlowListenerExecutor.getFlow())) {
                throw new RuntimeException("流程" + theFlowListenerExecutor.getFlow() + "存在多个特定流程监听器");
            }
            // 将执行器放入持有器中
            theFlowListenerExecutorMap.put(theFlowListenerExecutor.getFlow(), theFlowListenerExecutor);
        }
    }

    /**
     * 获取特定流程监听器执行器
     *
     * @param flow 流程名称
     * @return null 如果不存在对应的特定流程监听器执行器
     */
    public TheFlowListenerExecutor getTheFlowListenerExecutor(String flow) {
        return theFlowListenerExecutorMap.get(flow);
    }
}
