/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.service;

import org.bekit.common.transaction.TransactionManager;
import org.bekit.service.annotation.service.Service;
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
 * 服务持有器
 */
@Component
public class ServicesHolder {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TransactionManager transactionManager;
    // 服务执行器Map（key：服务名称）
    private final Map<String, ServiceExecutor> serviceExecutorMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@Service服务并解析）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Service.class);
        for (String beanName : beanNames) {
            // 解析服务
            ServiceExecutor serviceExecutor = ServiceParser.parseService(applicationContext.getBean(beanName), transactionManager);
            Assert.isTrue(!serviceExecutorMap.containsKey(serviceExecutor.getServiceName()), String.format("存在重名的服务[%s]", serviceExecutor.getServiceName()));
            // 将执行器放入持有器中
            serviceExecutorMap.put(serviceExecutor.getServiceName(), serviceExecutor);
        }
    }

    /**
     * 获取所有服务名称
     */
    public Set<String> getServiceNames() {
        return Collections.unmodifiableSet(serviceExecutorMap.keySet());
    }

    /**
     * 获取服务执行器
     *
     * @param service 服务名称
     * @throws IllegalArgumentException 如果不存在该服务
     */
    public ServiceExecutor getRequiredServiceExecutor(String service) {
        ServiceExecutor serviceExecutor = serviceExecutorMap.get(service);
        if (serviceExecutor == null) {
            throw new IllegalArgumentException(String.format("服务[%s]不存在", service));
        }
        return serviceExecutor;
    }
}
