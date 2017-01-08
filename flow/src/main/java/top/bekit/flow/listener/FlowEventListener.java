/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 23:52 创建
 */
package top.bekit.flow.listener;

import org.springframework.beans.factory.annotation.Autowired;
import top.bekit.flow.engine.TargetContext;

import java.util.List;

/**
 * 流程事件监听器（会被注册到spring容器中）
 */
public class FlowEventListener {

    @Autowired
    private FlowListenerHolder flowListenerHolder;

    /**
     * 监听节点选择事件
     *
     * @param flow          流程名称
     * @param node          被选择的节点名称
     * @param targetContext 目标上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void listenNodeDecide(String flow, String node, TargetContext targetContext) throws Throwable {
        // 获取该流程的所有监听器
        List<FlowListenerExecutor> flowListenerExecutors = flowListenerHolder.getFlowListenerExecutors(flow);
        for (FlowListenerExecutor flowListenerExecutor : flowListenerExecutors) {
            // 监听节点选择事件
            flowListenerExecutor.listenNodeDecide(node, targetContext);
        }
    }

}
