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
import top.bekit.flow.FlowEngine;
import top.bekit.flow.engine.DefaultFlowEngine;
import top.bekit.flow.flow.FlowHolder;
import top.bekit.flow.listener.FlowEventListener;
import top.bekit.flow.listener.FlowListenerHolder;
import top.bekit.flow.processor.ProcessorHolder;
import top.bekit.flow.transaction.FlowTxHolder;

/**
 * 流程引擎配置类
 * （非spring-boot项目需手动引入本配置类完成流程引擎配置）
 */
@Configuration
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

    // 流程事件监听器
    @Bean
    public FlowEventListener flowEventListener() {
        return new FlowEventListener();
    }

    // 流程监听器持有器
    @Bean
    public FlowListenerHolder flowListenerHolder() {
        return new FlowListenerHolder();
    }
}
