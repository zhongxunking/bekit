/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-31 18:50 创建
 */
package org.bekit.flow.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.bekit.flow.annotation.transaction.FlowTx;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程事务持有器（会被注册到spring容器中）
 */
public class FlowTxHolder {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    private PlatformTransactionManager txManager;
    // 流程事务执行器Map（key：流程事务对应的流程名称）
    private Map<String, FlowTxExecutor> flowTxExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@FlowTx流程事务并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(FlowTx.class);
        if (beanNames.length > 0 && txManager == null) {
            throw new RuntimeException("存在流程事务但是不存在事务管理器（PlatformTransactionManager），请检查是否有配置spring事务管理器");
        }
        for (String beanName : beanNames) {
            // 解析流程事务
            FlowTxExecutor flowTxExecutor = FlowTxParser.parseFlowTx(applicationContext.getBean(beanName), txManager);
            if (flowTxExecutorMap.containsKey(flowTxExecutor.getFlow())) {
                throw new RuntimeException("流程" + flowTxExecutor.getFlow() + "存在多个流程事务");
            }
            // 将执行器放入持有器中
            flowTxExecutorMap.put(flowTxExecutor.getFlow(), flowTxExecutor);
        }
    }

    /**
     * 获取流程事务执行器
     *
     * @param flow 流程名称
     * @throws RuntimeException 如果不存在该流程事务处理器
     */
    public FlowTxExecutor getRequiredFlowTxExecutor(String flow) {
        if (!flowTxExecutorMap.containsKey(flow)) {
            throw new RuntimeException("不存在流程" + flow + "的流程事务");
        }
        return flowTxExecutorMap.get(flow);
    }
}
