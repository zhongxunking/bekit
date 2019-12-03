/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.boot;

import org.bekit.event.boot.EventBusAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 服务引擎自动配置类
 */
@Configuration
@AutoConfigureAfter(EventBusAutoConfiguration.class)
@Import(ServiceEngineConfiguration.class)
public class ServiceEngineAutoConfiguration {
}
