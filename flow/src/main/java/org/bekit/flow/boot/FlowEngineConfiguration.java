/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 20:55 创建
 */
package org.bekit.flow.boot;

import org.bekit.common.scanner.AbstractScanner;
import org.bekit.common.transaction.TransactionManager;
import org.bekit.event.boot.EventBusConfiguration;
import org.bekit.event.bus.EventBusHub;
import org.bekit.flow.annotation.flow.Flow;
import org.bekit.flow.annotation.locker.TheFlowLocker;
import org.bekit.flow.annotation.mapper.TheFlowMapper;
import org.bekit.flow.annotation.processor.Processor;
import org.bekit.flow.engine.DefaultFlowEngine;
import org.bekit.flow.flow.FlowExecutor;
import org.bekit.flow.flow.FlowParser;
import org.bekit.flow.flow.FlowRegistrar;
import org.bekit.flow.listener.DefaultFlowListener;
import org.bekit.flow.locker.TheFlowLockerExecutor;
import org.bekit.flow.locker.TheFlowLockerParser;
import org.bekit.flow.locker.TheFlowLockerRegistrar;
import org.bekit.flow.mapper.TheFlowMapperExecutor;
import org.bekit.flow.mapper.TheFlowMapperParser;
import org.bekit.flow.mapper.TheFlowMapperRegistrar;
import org.bekit.flow.processor.ProcessorExecutor;
import org.bekit.flow.processor.ProcessorParser;
import org.bekit.flow.processor.ProcessorRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * 流程引擎配置
 */
@Configuration
@Import({EventBusConfiguration.class,
        DefaultFlowEngine.class,
        DefaultFlowListener.class,
        ProcessorRegistrar.class,
        TheFlowMapperRegistrar.class,
        TheFlowLockerRegistrar.class,
        FlowRegistrar.class,
        FlowEngineConfiguration.ProcessorScanner.class,
        FlowEngineConfiguration.TheFlowMapperScanner.class,
        FlowEngineConfiguration.TheFlowLockerScanner.class,
        FlowEngineConfiguration.FlowScanner.class})
public class FlowEngineConfiguration {
    /**
     * 优先级
     */
    public static final int ORDER = 0;

    /**
     * 处理器扫描器
     */
    @Order(ORDER)
    public static class ProcessorScanner extends AbstractScanner {
        // 处理器注册器
        private final ProcessorRegistrar processorRegistrar;

        public ProcessorScanner(ProcessorRegistrar processorRegistrar) {
            super(Processor.class);
            this.processorRegistrar = processorRegistrar;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            ProcessorExecutor processorExecutor = ProcessorParser.parseProcessor(obj);
            // 注册
            ProcessorExecutor existedOne = processorRegistrar.register(processorExecutor);
            Assert.isNull(existedOne, String.format("存在重名的处理器[%s]", processorExecutor.getProcessorName()));
        }
    }

    /**
     * 特定流程映射器扫描器
     */
    @Order(ORDER + 100)
    public static class TheFlowMapperScanner extends AbstractScanner {
        // 处理器注册器
        private final TheFlowMapperRegistrar theFlowMapperRegistrar;

        public TheFlowMapperScanner(TheFlowMapperRegistrar theFlowMapperRegistrar) {
            super(TheFlowMapper.class);
            this.theFlowMapperRegistrar = theFlowMapperRegistrar;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            TheFlowMapperExecutor theFlowMapperExecutor = TheFlowMapperParser.parseTheFlowMapper(obj);
            // 注册
            TheFlowMapperExecutor existedOne = theFlowMapperRegistrar.register(theFlowMapperExecutor);
            Assert.isNull(existedOne, String.format("流程[%s]存在重复的特定流程映射器", theFlowMapperExecutor.getFlow()));
        }
    }

    /**
     * 特定流程加锁器扫描器
     */
    @Order(ORDER + 200)
    public static class TheFlowLockerScanner extends AbstractScanner {
        // 处理器注册器
        private final TheFlowLockerRegistrar theFlowLockerRegistrar;

        public TheFlowLockerScanner(TheFlowLockerRegistrar theFlowLockerRegistrar) {
            super(TheFlowLocker.class);
            this.theFlowLockerRegistrar = theFlowLockerRegistrar;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            TheFlowLockerExecutor theFlowLockerExecutor = TheFlowLockerParser.parseTheFlowLocker(obj);
            // 注册
            TheFlowLockerExecutor existedOne = theFlowLockerRegistrar.register(theFlowLockerExecutor);
            Assert.isNull(existedOne, String.format("流程[%s]存在重复的特定流程加锁器", theFlowLockerExecutor.getFlow()));
        }
    }


    /**
     * 流程扫描器
     */
    @Order(ORDER + 300)
    public static class FlowScanner extends AbstractScanner {
        // 流程注册器
        private final FlowRegistrar flowRegistrar;
        // 处理器注册器
        private final ProcessorRegistrar processorRegistrar;
        // 特定流程映射器注册器
        private final TheFlowMapperRegistrar theFlowMapperRegistrar;
        // 特定流程加锁器注册器
        private final TheFlowLockerRegistrar theFlowLockerRegistrar;
        // 事务管理器
        private final TransactionManager transactionManager;
        // 事件总线中心
        private final EventBusHub eventBusHub;

        public FlowScanner(FlowRegistrar flowRegistrar,
                           ProcessorRegistrar processorRegistrar,
                           TheFlowMapperRegistrar theFlowMapperRegistrar,
                           TheFlowLockerRegistrar theFlowLockerRegistrar,
                           TransactionManager transactionManager,
                           EventBusHub eventBusHub) {
            super(Flow.class);
            this.flowRegistrar = flowRegistrar;
            this.processorRegistrar = processorRegistrar;
            this.theFlowMapperRegistrar = theFlowMapperRegistrar;
            this.theFlowLockerRegistrar = theFlowLockerRegistrar;
            this.transactionManager = transactionManager;
            this.eventBusHub = eventBusHub;
        }

        @Override
        protected void onScan(Object obj) {
            // 解析
            FlowExecutor flowExecutor = FlowParser.parseFlow(
                    obj,
                    processorRegistrar,
                    theFlowMapperRegistrar,
                    theFlowLockerRegistrar,
                    transactionManager,
                    eventBusHub);
            // 注册
            FlowExecutor existedOne = flowRegistrar.register(flowExecutor);
            Assert.isNull(existedOne, String.format("存在重名的流程[%s]", flowExecutor.getFlowName()));
        }
    }
}
