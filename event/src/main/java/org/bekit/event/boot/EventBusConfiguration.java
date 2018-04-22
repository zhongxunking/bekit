/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.boot;

import org.bekit.event.EventPublisher;
import org.bekit.event.bus.EventBusesHolder;
import org.bekit.event.extension.support.BizListenerType;
import org.bekit.event.listener.ListenersHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 事件总线配置类
 * （非spring-boot项目需手动引入本配置类完成事件总线配置）
 */
@Configuration
public class EventBusConfiguration {

    // 业务事件发布器
    @Bean
    public EventPublisher eventPublisher(EventBusesHolder eventBusesHolder) {
        return new DefaultEventPublisher(eventBusesHolder.getEventBus(BizListenerType.class));
    }

    // 事件总线持有器
    @Bean
    public EventBusesHolder eventBusesHolder() {
        return new EventBusesHolder();
    }

    // 监听器持有器
    @Bean
    public ListenersHolder listenersHolder() {
        return new ListenersHolder();
    }
}
