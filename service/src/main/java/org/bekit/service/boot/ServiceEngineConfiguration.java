/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.boot;

import org.bekit.common.scanner.AbstractScanner;
import org.bekit.common.transaction.TransactionManager;
import org.bekit.event.boot.EventBusConfiguration;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.engine.DefaultServiceEngine;
import org.bekit.service.service.ServiceExecutor;
import org.bekit.service.service.ServiceParser;
import org.bekit.service.service.ServiceRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * 服务引擎配置
 */
@Configuration
@Import({EventBusConfiguration.class,
        DefaultServiceEngine.class,
        ServiceRegistrar.class,
        ServiceEngineConfiguration.ServiceScanner.class})
public class ServiceEngineConfiguration {
    /**
     * 优先级
     */
    public static final int ORDER = 0;

    /**
     * 服务扫描器
     */
    @Order(ORDER)
    public static class ServiceScanner extends AbstractScanner {
        // 服务注册器
        private final ServiceRegistrar serviceRegistrar;
        // 事件总线持有器
        private final EventBusesHolder eventBusesHolder;
        // 事务管理器
        private final TransactionManager transactionManager;

        public ServiceScanner(ServiceRegistrar serviceRegistrar,
                              EventBusesHolder eventBusesHolder,
                              TransactionManager transactionManager) {
            super(Service.class);
            this.serviceRegistrar = serviceRegistrar;
            this.eventBusesHolder = eventBusesHolder;
            this.transactionManager = transactionManager;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            ServiceExecutor serviceExecutor = ServiceParser.parseService(obj, eventBusesHolder, transactionManager);
            // 注册
            ServiceExecutor existedOne = serviceRegistrar.register(serviceExecutor);
            Assert.isNull(existedOne, String.format("存在重名的服务[%s]", serviceExecutor.getServiceName()));
        }
    }
}
