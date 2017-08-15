/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.boot;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.bekit.event.boot.EventBusAutoConfiguration;

/**
 * 服务引擎自动配置类
 */
@Configuration
@AutoConfigureAfter(EventBusAutoConfiguration.class)
@Import(ServiceEngineConfiguration.class)
public class ServiceEngineAutoConfiguration {
    // 服务引擎由ServiceEngineConfiguration进行配置
    // 本配置类的作用就是在spring-boot项目中自动导入ServiceEngineConfiguration
}
