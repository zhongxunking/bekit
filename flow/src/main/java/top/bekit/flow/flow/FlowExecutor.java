/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 23:48 创建
 */
package top.bekit.flow.flow;

import top.bekit.common.method.MethodExecutor;
import top.bekit.event.EventPublisher;
import top.bekit.flow.engine.TargetContext;
import top.bekit.flow.event.FlowExceptionEvent;
import top.bekit.flow.event.NodeDecideEvent;
import top.bekit.flow.processor.ProcessorExecutor;
import top.bekit.flow.transaction.FlowTxExecutor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 流程执行器
 */
public class FlowExecutor {
    // 流程名称
    private String flowName;
    // 是否开启流程事务
    private boolean enableFlowTx;
    // 流程
    private Object flow;
    // 开始节点
    private String startNode;
    // 结束节点
    private Set<String> endNodes = new HashSet<>();
    // 等待节点（结束节点也属于等待节点）
    private Set<String> waitNodes = new HashSet<>();
    // 节点执行器Map（key：节点名称）
    private Map<String, NodeExecutor> nodeExecutorMap = new HashMap<>();
    // 目标对象映射执行器
    private TargetMappingExecutor mappingExecutor;
    // 流程事务执行器
    private FlowTxExecutor flowTxExecutor;
    // 事件发布器
    private EventPublisher eventPublisher;

    public FlowExecutor(String flowName, boolean enableFlowTx, Object flow, EventPublisher eventPublisher) {
        this.flowName = flowName;
        this.enableFlowTx = enableFlowTx;
        this.flow = flow;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 执行流程
     *
     * @param targetContext 目标上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void execute(TargetContext targetContext) throws Throwable {
        try {
            // 获取即将执行的节点
            String node = beforeStep(targetContext);
            if (!endNodes.contains(node)) {
                // 获取节点执行器
                NodeExecutor nodeExecutor = nodeExecutorMap.get(node);
                do {
                    // 执行节点
                    String nextNode = nodeExecutor.execute(flow, targetContext);
                    // 判断是否中断流程
                    if (nextNode == null) {
                        break;
                    }
                    if (!nodeExecutorMap.containsKey(nextNode)) {
                        throw new RuntimeException("流程" + flowName + "不存在下一个节点" + nextNode);
                    }
                    // 发送节点选择事件
                    eventPublisher.publish(new NodeDecideEvent(flowName, nextNode, targetContext));
                    // 判断是否提交事务
                    if (enableFlowTx && nodeExecutor.isCommitTx() && !waitNodes.contains(nextNode)) {
                        afterStep();
                        // 获取即将执行的节点（防止事务提交后目标对象被其他线程抢占被执行到其他节点，此处就是更新到最新节点）
                        nextNode = beforeStep(targetContext);
                    }
                    // 获取下一个节点执行器
                    nodeExecutor = nodeExecutorMap.get(nextNode);
                } while (nodeExecutor.isAutoExecute());
            }
            afterStep();
        } catch (Throwable e) {
            afterThrowing(e, targetContext);
            throw e;
        }
    }

    // 在每一个步骤执行前执行
    private String beforeStep(TargetContext targetContext) throws Throwable {
        if (enableFlowTx) {
            // 创建事务
            flowTxExecutor.createTx();
            // 锁目标对象
            flowTxExecutor.lockTarget(targetContext);
        }
        // 返回接下来需要执行的节点
        return targetMappingToNode(targetContext);
    }

    // 在每一个步骤执行后执行
    private void afterStep() {
        if (enableFlowTx) {
            // 提交事务
            flowTxExecutor.commitTx();
        }
    }

    // 在发生异常后执行
    private void afterThrowing(Throwable throwable, TargetContext targetContext) {
        try {
            if (enableFlowTx) {
                // 回滚事务
                flowTxExecutor.rollbackTx();
            }
        } finally {
            // 发送流程异常事件
            eventPublisher.publish(new FlowExceptionEvent(flowName, throwable, targetContext));
        }
    }

    // 目标对象映射到节点
    private String targetMappingToNode(TargetContext targetContext) throws Throwable {
        // 执行映射执行器
        String node = mappingExecutor.execute(flow, targetContext);
        if (!nodeExecutorMap.containsKey(node)) {
            throw new RuntimeException("流程" + flowName + "不存在节点" + node);
        }

        return node;
    }

    /**
     * 添加节点
     *
     * @param nodeExecutor 节点执行器
     * @throws IllegalStateException 如果相同名称的节点执行器已存在
     */
    public void addNode(NodeExecutor nodeExecutor) {
        if (nodeExecutorMap.containsKey(nodeExecutor.getNodeName())) {
            throw new IllegalStateException("流程" + flowName + "存在同名的节点" + nodeExecutor.getNodeName());
        }
        nodeExecutorMap.put(nodeExecutor.getNodeName(), nodeExecutor);
        if (!nodeExecutor.isAutoExecute()) {
            waitNodes.add(nodeExecutor.getNodeName());
        }
    }

    /**
     * 设置开始节点
     *
     * @throws IllegalStateException 如果开始节点已存在
     */
    public void setStartNode(String startNode) {
        if (this.startNode != null) {
            throw new IllegalStateException("流程" + flowName + "存在多个开始节点");
        }
        this.startNode = startNode;
    }

    /**
     * 添加结束节点
     */
    public void addEndNode(String endNode) {
        endNodes.add(endNode);
    }

    /**
     * 设置目标对象映射执行器
     *
     * @param mappingExecutor 目标对象映射执行器
     * @throws IllegalStateException 如果目标对象映射执行器已经被设置过
     */
    public void setMappingExecutor(TargetMappingExecutor mappingExecutor) {
        if (this.mappingExecutor != null) {
            throw new IllegalStateException("流程" + flowName + "存在多个目标对象映射方法（@TargetMapping类型方法）");
        }
        this.mappingExecutor = mappingExecutor;
    }

    /**
     * 设置流程事务执行器
     *
     * @param flowTxExecutor 流程事务执行器
     * @throws IllegalStateException 如果流程事务执行器不能被设置或已经被设置
     */
    public void setFlowTxExecutor(FlowTxExecutor flowTxExecutor) {
        if (!enableFlowTx) {
            throw new IllegalStateException("流程" + flowName + "的enableFlowTx属性为关闭状态，不能设置流程事务");
        }
        if (this.flowTxExecutor != null) {
            throw new IllegalStateException("流程" + flowName + "的流程事务执行器已被设置，不能重复设置");
        }
        this.flowTxExecutor = flowTxExecutor;
    }

    /**
     * 校验流程执行器是否有效
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (flowName == null || flow == null || eventPublisher == null) {
            throw new IllegalStateException("流程" + flowName + "内部要素不全");
        }
        if (startNode == null) {
            throw new IllegalStateException("流程" + flowName + "缺少开始节点");
        }
        if (endNodes.isEmpty()) {
            throw new IllegalStateException("流程" + flowName + "没有结束节点");
        }
        if (mappingExecutor == null) {
            throw new IllegalStateException("流程" + flowName + "缺少目标对象映射方法（@TargetMapping类型方法）");
        }
        if (enableFlowTx) {
            if (flowTxExecutor == null) {
                throw new IllegalStateException("流程" + flowName + "的enableFlowTx属性为开启状态，但未设置对应的流程事务");
            }
        } else {
            if (flowTxExecutor != null) {
                throw new IllegalStateException("流程" + flowName + "的enableFlowTx属性为关闭状态，但设置了流程事务");
            }
        }
    }

    /**
     * 获取流程名称
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * 节点执行器
     */
    public static class NodeExecutor {
        // 节点名称
        private String nodeName;
        // 处理器执行器
        private ProcessorExecutor processorExecutor;
        // 是否自动执行本节点
        private boolean autoExecute;
        // 本节点执行完后是否提交事务
        private boolean commitTx;
        // 节点决策器执行器
        private NodeDeciderExecutor nodeDeciderExecutor;

        public NodeExecutor(String nodeName, ProcessorExecutor processorExecutor, boolean autoExecute, boolean commitTx) {
            this.nodeName = nodeName;
            this.processorExecutor = processorExecutor;
            this.autoExecute = autoExecute;
            this.commitTx = commitTx;
        }

        /**
         * 执行节点
         *
         * @param flow          流程
         * @param targetContext 目标上下文
         * @return 下个节点
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public String execute(Object flow, TargetContext targetContext) throws Throwable {
            Object processResult = null;
            if (processorExecutor != null) {
                // 执行节点处理器
                processResult = processorExecutor.execute(targetContext);
            }
            // 执行节点决策器
            return nodeDeciderExecutor.execute(flow, processResult, targetContext);
        }

        /**
         * 设置节点决策器执行器
         */
        public void setNodeDeciderExecutor(NodeDeciderExecutor nodeDeciderExecutor) {
            if (this.nodeDeciderExecutor != null) {
                throw new IllegalStateException("节点" + nodeName + "已设置节点决策器执行器，不能重复设置");
            }
            this.nodeDeciderExecutor = nodeDeciderExecutor;
        }

        /**
         * 本节点是否自动执行
         */
        public boolean isAutoExecute() {
            return autoExecute;
        }

        /**
         * 本节点执行完后是否提交事务
         */
        public boolean isCommitTx() {
            return commitTx;
        }

        /**
         * 校验节点执行器是否有效
         *
         * @throws IllegalStateException 如果校验不通过
         */
        public void validate() {
            if (nodeName == null || nodeDeciderExecutor == null) {
                throw new IllegalStateException("节点" + nodeName + "内部要素不全");
            }
        }

        /**
         * 获取节点名称
         */
        public String getNodeName() {
            return nodeName;
        }

        /**
         * 节点决策器执行器（选出下个节点）
         */
        public static class NodeDeciderExecutor extends MethodExecutor {
            // 参数类型
            private ParametersType parametersType;

            public NodeDeciderExecutor(Method targetMethod, ParametersType parametersType) {
                super(targetMethod);
                this.parametersType = parametersType;
            }

            /**
             * 执行
             *
             * @param flow          流程
             * @param processResult 处理器执行结果
             * @param targetContext 目标上下文
             * @return 下个节点名称
             * @throws Throwable 执行过程中发生任何异常都会往外抛
             */
            public String execute(Object flow, Object processResult, TargetContext targetContext) throws Throwable {
                switch (parametersType) {
                    case NONE:
                        return (String) execute(flow, new Object[]{});
                    case ONLY_PROCESS_RESULT:
                        return (String) execute(flow, new Object[]{processResult});
                    case ONLY_TARGET_CONTEXT:
                        return (String) execute(flow, new Object[]{targetContext});
                    case PROCESS_RESULT_AND_TARGET_CONTEXT:
                        return (String) execute(flow, new Object[]{processResult, targetContext});
                    default:
                        throw new IllegalStateException("下个节点选择方法执行器内部状态不对");
                }
            }

            /**
             * 下个节点选择方法参数类型
             */
            public enum ParametersType {
                /**
                 * 无参数
                 */
                NONE,
                /**
                 * 只有处理结果参数
                 */
                ONLY_PROCESS_RESULT,
                /**
                 * 只有目标上下文
                 */
                ONLY_TARGET_CONTEXT,
                /**
                 * 处理结果和目标上下文都有
                 */
                PROCESS_RESULT_AND_TARGET_CONTEXT,;
            }
        }
    }

    /**
     * 目标对象映射执行器
     */
    public static class TargetMappingExecutor extends MethodExecutor {

        public TargetMappingExecutor(Method targetMethod) {
            super(targetMethod);
        }

        /**
         * 执行节点映射方法
         *
         * @param flow          流程
         * @param targetContext 目标上下文
         * @return 映射到的流程节点名称
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public String execute(Object flow, TargetContext targetContext) throws Throwable {
            return (String) execute(flow, new Object[]{targetContext.getTarget()});
        }
    }
}
