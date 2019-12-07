/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.boot;

import org.bekit.common.boot.CommonConfiguration;
import org.bekit.event.EventPublisher;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.event.extension.support.DomainListenerType;
import org.bekit.event.listener.ListenersHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * 事件总线配置
 */
@Configuration
@Import({CommonConfiguration.class, EventBusesHolder.class, ListenersHolder.class})
public class EventBusConfiguration {
    // 领域事件发布器
    @Bean
    @DependsOn("org.bekit.event.bus.EventBusesHolder")      // 保证出现循环引用时不会出错
    public EventPublisher eventPublisher(EventBusesHolder eventBusesHolder) {
        return new DefaultEventPublisher(eventBusesHolder.getEventBus(DomainListenerType.class));
    }
}
