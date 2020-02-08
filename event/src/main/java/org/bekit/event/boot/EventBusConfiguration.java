/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.boot;

import lombok.AllArgsConstructor;
import org.bekit.common.boot.CommonConfiguration;
import org.bekit.common.scanner.AbstractScanner;
import org.bekit.event.EventPublisher;
import org.bekit.event.annotation.listener.Listener;
import org.bekit.event.bus.EventBus;
import org.bekit.event.bus.EventBusHub;
import org.bekit.event.extension.ListenerType;
import org.bekit.event.extension.support.DomainListenerType;
import org.bekit.event.listener.ListenerExecutor;
import org.bekit.event.listener.ListenerHub;
import org.bekit.event.listener.ListenerParser;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

/**
 * 事件总线配置
 */
@Configuration
@Import({CommonConfiguration.class,
        EventBusHub.class,
        ListenerHub.class,
        EventBusConfiguration.ListenerScanner.class,
        EventBusConfiguration.EventBusInitializer.class})
public class EventBusConfiguration {
    /**
     * 优先级
     */
    public static final int ORDER = 0;

    // 配置领域事件发布器
    @Bean
    public EventPublisher eventPublisher(EventBusHub eventBusHub) {
        return new DefaultEventPublisher(eventBusHub.getEventBus(DomainListenerType.class));
    }

    /**
     * 监听器扫描器
     */
    @Order(ORDER)
    public static class ListenerScanner extends AbstractScanner {
        // 监听器中心
        private final ListenerHub listenerHub;

        public ListenerScanner(ListenerHub listenerHub) {
            super(Listener.class);
            this.listenerHub = listenerHub;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            ListenerExecutor listenerExecutor = ListenerParser.parseListener(obj);
            // 注册
            listenerHub.addListener(listenerExecutor);
        }
    }

    /**
     * 事件总线初始化器
     */
    @Order(ORDER + 100)
    @AllArgsConstructor
    public static class EventBusInitializer implements ApplicationListener<ContextRefreshedEvent> {
        // 数据总线中心
        private final EventBusHub eventBusHub;
        // 监听器中心
        private final ListenerHub listenerHub;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            for (Class<? extends ListenerType> type : listenerHub.getTypes()) {
                // 初始化事件总线
                EventBus eventBus = eventBusHub.getEventBus(type);
                for (ListenerExecutor listenerExecutor : listenerHub.getListeners(type)) {
                    eventBus.addListenerExecutor(listenerExecutor);
                }
            }
        }
    }
}
