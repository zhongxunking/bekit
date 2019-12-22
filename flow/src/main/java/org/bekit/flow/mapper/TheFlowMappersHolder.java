/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 17:45 创建
 */
package org.bekit.flow.mapper;

import org.bekit.flow.annotation.mapper.TheFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 特定流程映射器持有器
 */
@Component
public class TheFlowMappersHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 特定流程映射器执行器Map（key：映射的流程名称）
    private final Map<String, TheFlowMapperExecutor> theFlowMapperExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的特定流程映射器并解析）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(TheFlowMapper.class);
        for (String beanName : beanNames) {
            // 解析特定流程映射器
            TheFlowMapperExecutor theFlowMapperExecutor = TheFlowMapperParser.parseTheFlowMapper(applicationContext.getBean(beanName));
            Assert.isTrue(!theFlowMapperExecutorMap.containsKey(theFlowMapperExecutor.getFlow()), String.format("流程[%s]存在重复的特定流程映射器", theFlowMapperExecutor.getFlow()));
            // 将执行器放入持有器中
            theFlowMapperExecutorMap.put(theFlowMapperExecutor.getFlow(), theFlowMapperExecutor);
        }
    }

    /**
     * 获取特定流程映射器执行器
     *
     * @param flow 映射的流程名称
     * @return 特定流程映射器执行器
     */
    public TheFlowMapperExecutor getTheFlowMapperExecutor(String flow) {
        return theFlowMapperExecutorMap.get(flow);
    }
}
