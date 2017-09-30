/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 20:55 创建
 */
package org.bekit.flow.boot;

import org.bekit.event.boot.EventBusConfiguration;
import org.bekit.flow.FlowEngine;
import org.bekit.flow.engine.DefaultFlowEngine;
import org.bekit.flow.flow.FlowHolder;
import org.bekit.flow.listener.DefaultFlowEventListener;
import org.bekit.flow.processor.ProcessorHolder;
import org.bekit.flow.transaction.FlowTxHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 流程引擎配置类
 * （非spring-boot项目需手动引入本配置类完成流程引擎配置）
 */
@Configuration
@Import(EventBusConfiguration.class)
public class FlowEngineConfiguration {

    // 流程引擎
    @Bean
    public FlowEngine flowEngine() {
        return new DefaultFlowEngine();
    }

    // 流程持有器
    @Bean
    public FlowHolder flowHolder() {
        return new FlowHolder();
    }

    // 处理器持有器
    @Bean
    public ProcessorHolder processorHolder() {
        return new ProcessorHolder();
    }

    // 流程事务持有器
    @Bean
    public FlowTxHolder flowTxHolder() {
        return new FlowTxHolder();
    }

    // 默认的流程事件监听器
    @Bean
    public DefaultFlowEventListener defaultFlowEventListener() {
        return new DefaultFlowEventListener();
    }
}
