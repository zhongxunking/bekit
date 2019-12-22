/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 19:19 创建
 */
package org.bekit.flow.locker;

import org.bekit.flow.annotation.locker.TheFlowLocker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 特定流程加锁器持有器
 */
@Component
public class TheFlowLockersHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 特定流程加锁器执行器Map（key：加锁的流程名称）
    private final Map<String, TheFlowLockerExecutor> theFlowLockerExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的特定流程加锁器并解析）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(TheFlowLocker.class);
        for (String beanName : beanNames) {
            // 解析特定流程加锁器
            TheFlowLockerExecutor theFlowLockerExecutor = TheFlowLockerParser.parseTheFlowLocker(applicationContext.getBean(beanName));
            Assert.isTrue(!theFlowLockerExecutorMap.containsKey(theFlowLockerExecutor.getFlow()), String.format("流程[%s]存在重复的特定流程加锁器", theFlowLockerExecutor.getFlow()));
            // 将执行器放入持有器中
            theFlowLockerExecutorMap.put(theFlowLockerExecutor.getFlow(), theFlowLockerExecutor);
        }
    }

    /**
     * 获取特定流程加锁器
     *
     * @param flow 加锁的流程名称
     * @return 特定流程加锁器
     */
    public TheFlowLockerExecutor getTheFlowLockerExecutor(String flow) {
        return theFlowLockerExecutorMap.get(flow);
    }
}
