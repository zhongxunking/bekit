/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.bekit.event.boot.EventBusConfiguration;
import top.bekit.event.bus.EventBusHolder;
import top.bekit.event.publisher.DefaultEventPublisher;
import top.bekit.service.ServiceEngine;
import top.bekit.service.annotation.listener.ServiceListener;
import top.bekit.service.engine.DefaultServiceEngine;
import top.bekit.service.service.ServiceHolder;

/**
 * 服务引擎配置类
 * （非spring-boot项目需手动引入本配置类完成服务引擎配置）
 */
@Configuration
@Import(EventBusConfiguration.class)
public class ServiceEngineConfiguration {

    // 服务引擎
    @Bean
    public ServiceEngine serviceEngine(EventBusHolder eventBusHolder) {
        return new DefaultServiceEngine(new DefaultEventPublisher(eventBusHolder.getEventBus(ServiceListener.class)));
    }

    // 服务持有器
    @Bean
    public ServiceHolder serviceHolder() {
        return new ServiceHolder();
    }
}
