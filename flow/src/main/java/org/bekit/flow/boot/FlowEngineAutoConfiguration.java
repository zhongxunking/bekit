/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 00:36 创建
 */
package org.bekit.flow.boot;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.bekit.event.boot.EventBusAutoConfiguration;

/**
 * 流程引擎自动配置类
 */
@Configuration
@AutoConfigureAfter(EventBusAutoConfiguration.class)
@Import(FlowEngineConfiguration.class)
public class FlowEngineAutoConfiguration {
    // 流程引擎由FlowEngineConfiguration进行配置
    // 本配置类的作用就是在spring-boot项目中自动导入FlowEngineConfiguration
}
