/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:40 创建
 */
package org.bekit.flow.flow;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bekit.common.transaction.TransactionManager;
import org.bekit.common.transaction.TxExecutor;
import org.bekit.event.bus.EventBusHub;
import org.bekit.event.publisher.DefaultEventPublisher;
import org.bekit.flow.annotation.flow.EndNode;
import org.bekit.flow.annotation.flow.Flow;
import org.bekit.flow.annotation.flow.Node;
import org.bekit.flow.annotation.flow.StartNode;
import org.bekit.flow.engine.FlowContext;
import org.bekit.flow.listener.FlowListenerType;
import org.bekit.flow.locker.TheFlowLockerRegistrar;
import org.bekit.flow.mapper.TheFlowMapperRegistrar;
import org.bekit.flow.processor.ProcessorExecutor;
import org.bekit.flow.processor.ProcessorRegistrar;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程解析器
 */
@Slf4j
public class FlowParser {
    /**
     * 解析流程
     *
     * @param flow               流程
     * @param processorRegistrar 处理器注册器
     * @param mapperRegistrar    映射器注册器
     * @param lockerRegistrar    加锁器注册器
     * @param transactionManager 事务管理器
     * @param eventBusHub        事件总线中心
     * @return 流程执行器
     */
    public static FlowExecutor parseFlow(Object flow,
                                         ProcessorRegistrar processorRegistrar,
                                         TheFlowMapperRegistrar mapperRegistrar,
                                         TheFlowLockerRegistrar lockerRegistrar,
                                         TransactionManager transactionManager,
                                         EventBusHub eventBusHub) {
        // 获取目标class（应对AOP代理情况）
        Class<?> flowClass = AopUtils.getTargetClass(flow);
        log.debug("解析流程：{}", flowClass);
        // 解析流程名称
        Flow flowAnnotation = AnnotatedElementUtils.findMergedAnnotation(flowClass, Flow.class);
        String flowName = flowAnnotation.name();
        if (StringUtils.isEmpty(flowName)) {
            flowName = ClassUtils.getShortNameAsProperty(flowClass);
        }
        // 解析出所有节点执行器
        Map<Class<?>, Map<String, FlowExecutor.NodeExecutor>> map = parseToNodeExecutors(flowClass, processorRegistrar);

        return new FlowExecutor(
                flowName,
                flow,
                map.get(StartNode.class).keySet().iterator().next(),
                map.get(EndNode.class).keySet(),
                map.get(Node.class),
                mapperRegistrar.get(flowName),
                lockerRegistrar.get(flowName),
                new TxExecutor(transactionManager, TransactionManager.TransactionType.REQUIRED),
                new DefaultEventPublisher(eventBusHub.getEventBus(FlowListenerType.class)));
    }

    // 解析出所有节点执行器
    private static Map<Class<?>, Map<String, FlowExecutor.NodeExecutor>> parseToNodeExecutors(Class<?> flowClass, ProcessorRegistrar processorRegistrar) {
        Map<Class<?>, Map<String, FlowExecutor.NodeExecutor>> map = new HashMap<>();
        map.put(Node.class, new HashMap<>());
        map.put(StartNode.class, new HashMap<>());
        map.put(EndNode.class, new HashMap<>());
        // 解析
        ReflectionUtils.doWithLocalMethods(flowClass, method -> {
            Node nodeAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Node.class);
            if (nodeAnnotation != null) {
                FlowExecutor.NodeExecutor nodeExecutor = parseNode(nodeAnnotation, method, processorRegistrar);
                Map<String, FlowExecutor.NodeExecutor> nodeExecutorMap = map.get(Node.class);
                Assert.isTrue(!nodeExecutorMap.containsKey(nodeExecutor.getNodeName()), String.format("流程[%s]存在重名的节点[%s]", flowClass, nodeExecutor.getNodeName()));
                nodeExecutorMap.put(nodeExecutor.getNodeName(), nodeExecutor);
                // 判断是否是@StartNode、@EndNode
                if (AnnotatedElementUtils.findMergedAnnotation(method, StartNode.class) != null) {
                    map.get(StartNode.class).put(nodeExecutor.getNodeName(), nodeExecutor);
                } else if (AnnotatedElementUtils.findMergedAnnotation(method, EndNode.class) != null) {
                    map.get(EndNode.class).put(nodeExecutor.getNodeName(), nodeExecutor);
                }
            }
        });
        Assert.isTrue(map.get(StartNode.class).size() == 1, String.format("流程[%s]必须存在唯一的一个开始节点（@StartNode）", flowClass));

        return map;
    }

    // 解析节点
    private static FlowExecutor.NodeExecutor parseNode(Node nodeAnnotation, Method nodeMethod, ProcessorRegistrar processorRegistrar) {
        log.debug("解析流程节点：node={}，method={}", nodeAnnotation, nodeMethod);
        // 获取节点名称
        String nodeName = nodeAnnotation.name();
        if (StringUtils.isEmpty(nodeName)) {
            nodeName = nodeMethod.getName();
        }
        // 获取处理器
        ProcessorExecutor processorExecutor = null;
        if (StringUtils.isNotEmpty(nodeAnnotation.processor())) {
            processorExecutor = processorRegistrar.get(nodeAnnotation.processor());
            Assert.notNull(processorExecutor, String.format("不存在处理器[%s]", processorExecutor.getProcessorName()));
        }
        // 解析节点决策器
        FlowExecutor.NodeExecutor.NodeDeciderExecutor nodeDeciderExecutor = parseNodeDecider(nodeMethod, processorExecutor);

        return new FlowExecutor.NodeExecutor(
                nodeName,
                nodeAnnotation.haveState(),
                nodeAnnotation.autoExecute(),
                processorExecutor,
                nodeDeciderExecutor);
    }

    // 解析节点决策器
    private static FlowExecutor.NodeExecutor.NodeDeciderExecutor parseNodeDecider(Method nodeDeciderMethod, ProcessorExecutor processorExecutor) {
        // 校验方法类型
        Assert.isTrue(Modifier.isPublic(nodeDeciderMethod.getModifiers()), String.format("节点决策器[%s]必须是public类型", nodeDeciderMethod));
        // 判断+校验入参类型，可以存在的入参类型：()、(FlowContext)、(T)、(T, FlowContext)————T表示能被处理器返回结果赋值的类型
        FlowExecutor.NodeExecutor.NodeDeciderExecutor.ParameterType parameterType;
        Class[] parameterTypes = nodeDeciderMethod.getParameterTypes();
        if (parameterTypes.length == 0) {
            // 入参类型：()
            parameterType = FlowExecutor.NodeExecutor.NodeDeciderExecutor.ParameterType.NONE;
        } else {
            Assert.isTrue(AnnotatedElementUtils.findMergedAnnotation(nodeDeciderMethod, EndNode.class) == null, String.format("结束节点的决策器[%s]的入参类型必须为()", nodeDeciderMethod));
            if (parameterTypes.length == 1) {
                if (parameterTypes[0] == FlowContext.class) {
                    // 入参类型：(FlowContext)
                    parameterType = FlowExecutor.NodeExecutor.NodeDeciderExecutor.ParameterType.ONLY_FLOW_CONTEXT;
                } else {
                    // 入参类型：(T)
                    Assert.isTrue(processorExecutor != null, String.format("节点决策器[%s]不能有非FlowContext入参，因为这个节点没有处理器", nodeDeciderMethod));
                    Assert.isAssignable(parameterTypes[0], processorExecutor.getReturnType(), String.format("节点决策器[%s]的入参类型必须能被其处理器返回类型赋值", nodeDeciderMethod));
                    parameterType = FlowExecutor.NodeExecutor.NodeDeciderExecutor.ParameterType.ONLY_PROCESS_RESULT;
                }
            } else if (parameterTypes.length == 2) {
                // 入参类型：(T, FlowContext)
                Assert.isTrue(processorExecutor != null, String.format("节点决策器[%s]不能有非FlowContext入参，因为这个节点没有处理器", nodeDeciderMethod));
                Assert.isAssignable(parameterTypes[0], processorExecutor.getReturnType(), String.format("节点决策器[%s]的第一个入参类型必须能被其处理器返回类型赋值", nodeDeciderMethod));
                Assert.isTrue(parameterTypes[1] == FlowContext.class, String.format("节点决策器[%s]的第二个入参类型必须是FlowContext", nodeDeciderMethod));
                parameterType = FlowExecutor.NodeExecutor.NodeDeciderExecutor.ParameterType.PROCESS_RESULT_AND_FLOW_CONTEXT;
            } else {
                throw new IllegalArgumentException(String.format("节点决策器[%s]的入参类型必须为：()、(FlowContext)、(T)、(T, FlowContext)————T表示能被处理器返回结果赋值的类型", nodeDeciderMethod));
            }
        }
        // 校验返回类型
        if (AnnotatedElementUtils.findMergedAnnotation(nodeDeciderMethod, EndNode.class) == null) {
            Assert.isTrue(nodeDeciderMethod.getReturnType() == String.class, String.format("节点决策器[%s]的返回类型必须是String", nodeDeciderMethod));
        } else {
            Assert.isTrue(nodeDeciderMethod.getReturnType() == void.class, String.format("结束节点（@EndNode）的决策器[%s]的返回类型必须是void", nodeDeciderMethod));
        }

        return new FlowExecutor.NodeExecutor.NodeDeciderExecutor(parameterType, nodeDeciderMethod);
    }
}
