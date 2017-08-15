/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.boot;

import org.bekit.event.bus.EventBusHolder;
import org.bekit.event.listener.ListenerHolder;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.bekit.event.EventPublisher;
import org.bekit.event.annotation.BizListener;

/**
 * 事件总线配置类
 * （非spring-boot项目需手动引入本配置类完成事件总线配置）
 */
@Configuration
public class EventBusConfiguration {

    // 业务事件发布器
    @Bean
    public EventPublisher eventPublisher(EventBusHolder eventBusHolder) {
        return new DefaultEventPublisher(eventBusHolder.getEventBus(BizListener.class));
    }

    // 事件总线持有器
    @Bean
    public EventBusHolder eventBusHolder() {
        return new EventBusHolder();
    }

    // 监听器持有器
    @Bean
    public ListenerHolder listenerHolder() {
        return new ListenerHolder();
    }

}
