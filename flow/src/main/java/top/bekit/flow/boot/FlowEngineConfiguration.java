/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 20:55 创建
 */
package top.bekit.flow.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.bekit.event.boot.EventBusConfiguration;
import top.bekit.flow.FlowEngine;
import top.bekit.flow.engine.DefaultFlowEngine;
import top.bekit.flow.flow.FlowHolder;
import top.bekit.flow.listener.DefaultFlowEventListener;
import top.bekit.flow.listener.TheFlowListenerHolder;
import top.bekit.flow.processor.ProcessorHolder;
import top.bekit.flow.transaction.FlowTxHolder;

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

    // 特定流程监听器持有期
    @Bean
    public TheFlowListenerHolder theFlowListenerHolder() {
        return new TheFlowListenerHolder();
    }

}
