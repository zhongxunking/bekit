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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

/**
 * 事件总线配置类
 * （非spring-boot项目需手动引入本配置类完成事件总线配置）
 */
@Configuration
@Import({EventBusesHolder.class, ListenersHolder.class})
public class EventBusConfiguration {

    // 业务事件发布器
    @Bean
    @DependsOn("org.bekit.event.bus.EventBusesHolder")      // 保证出现循环引用时不会出错
    public EventPublisher eventPublisher(EventBusesHolder eventBusesHolder) {
        return new DefaultEventPublisher(eventBusesHolder.getEventBus(BizListenerType.class));
    }
}
