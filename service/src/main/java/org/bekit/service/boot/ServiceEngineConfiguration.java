/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.boot;

import org.bekit.event.EventPublisher;
import org.bekit.event.boot.EventBusConfiguration;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.bekit.service.ServiceEngine;
import org.bekit.service.engine.DefaultServiceEngine;
import org.bekit.service.listener.ServiceListenerType;
import org.bekit.service.service.ServicesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * 服务引擎配置
 */
@Configuration
@Import({EventBusConfiguration.class, ServicesHolder.class})
public class ServiceEngineConfiguration {
    // 服务引擎
    @Bean
    @DependsOn({"org.bekit.service.service.ServicesHolder", "org.bekit.event.bus.EventBusesHolder"})    // 保证出现循环引用时不会出错
    public ServiceEngine serviceEngine(ServicesHolder servicesHolder, EventBusesHolder eventBusesHolder) {
        EventPublisher eventPublisher = new DefaultEventPublisher(eventBusesHolder.getEventBus(ServiceListenerType.class));
        return new DefaultServiceEngine(servicesHolder, eventPublisher);
    }
}
