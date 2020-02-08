/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 00:36 创建
 */
package org.bekit.flow.boot;

import org.bekit.event.boot.EventBusAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 流程引擎自动配置
 */
@Configuration
@AutoConfigureAfter(EventBusAutoConfiguration.class)
@Import(FlowEngineConfiguration.class)
public class FlowEngineAutoConfiguration {
}
