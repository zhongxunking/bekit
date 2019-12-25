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
import org.bekit.event.EventPublisher;
import org.bekit.event.boot.EventBusConfiguration;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.bekit.service.ServiceEngine;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.engine.DefaultServiceEngine;
import org.bekit.service.listener.ServiceListenerType;
import org.bekit.service.service.ServiceExecutor;
import org.bekit.service.service.ServiceParser;
import org.bekit.service.service.ServiceRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * 服务引擎配置
 */
@Configuration
@Import({EventBusConfiguration.class,
        ServiceRegistrar.class,
        ServiceEngineConfiguration.ServiceScanner.class})
public class ServiceEngineConfiguration {
    /**
     * 优先级
     */
    public static final int ORDER = 0;

    // 配置服务引擎
    @Bean
    public ServiceEngine serviceEngine(ServiceRegistrar serviceRegistrar, EventBusesHolder eventBusesHolder) {
        EventPublisher eventPublisher = new DefaultEventPublisher(eventBusesHolder.getEventBus(ServiceListenerType.class));
        return new DefaultServiceEngine(serviceRegistrar, eventPublisher);
    }

    /**
     * 服务扫描器
     */
    @Order(ORDER)
    public static class ServiceScanner extends AbstractScanner {
        // 服务注册器
        private final ServiceRegistrar serviceRegistrar;
        // 事务管理器
        private final TransactionManager transactionManager;

        public ServiceScanner(ServiceRegistrar serviceRegistrar, TransactionManager transactionManager) {
            super(Service.class);
            this.serviceRegistrar = serviceRegistrar;
            this.transactionManager = transactionManager;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            ServiceExecutor serviceExecutor = ServiceParser.parseService(obj, transactionManager);
            // 注册
            ServiceExecutor existedOne = serviceRegistrar.register(serviceExecutor);
            Assert.isNull(existedOne, String.format("存在重名的服务[%s]", serviceExecutor.getServiceName()));
        }
    }
}
