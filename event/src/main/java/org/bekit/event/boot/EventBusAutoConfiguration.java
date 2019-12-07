/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.boot;

import org.bekit.common.boot.CommonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 事件总线自动配置
 */
@Configuration
@AutoConfigureAfter(CommonAutoConfiguration.class)
@Import(EventBusConfiguration.class)
public class EventBusAutoConfiguration {
}
