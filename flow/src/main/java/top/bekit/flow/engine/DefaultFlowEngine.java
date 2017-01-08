/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:49 创建
 */
package top.bekit.flow.engine;

import org.springframework.beans.factory.annotation.Autowired;
import top.bekit.flow.FlowEngine;
import top.bekit.flow.flow.FlowExecutor;
import top.bekit.flow.flow.FlowHolder;
import top.bekit.flow.transaction.FlowTxHolder;

import java.util.Map;

/**
 * 流程引擎默认实现类
 */
public class DefaultFlowEngine implements FlowEngine {
    @Autowired
    private FlowHolder flowHolder;
    @Autowired
    private FlowTxHolder flowTxHolder;

    @Override
    public void start(String flow, Object target) {
        start(flow, target, null);
    }

    @Override
    public void start(String flow, Object target, Map<Object, Object> attachment) {
        try {
            // 构造目标上下文
            TargetContext targetContext = new TargetContext(target, attachment);
            // 获取流程执行器
            FlowExecutor flowExecutor = flowHolder.getRequiredFlowExecutor(flow);
            // 执行流程
            flowExecutor.execute(targetContext);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            // 包装异常为RuntimeException异常，让外部不用每次调用时都需要catch
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertTargetAndStart(String flow, Object target, Map<Object, Object> attachment) {
        try {
            // 构造目标上下文
            TargetContext targetContext = new TargetContext(target, attachment);
            //以新事务插入目标对象到数据库并提交
            flowTxHolder.getRequiredFlowTxExecutor(flow).insertTarget(targetContext);
            // 获取流程执行器
            FlowExecutor flowExecutor = flowHolder.getRequiredFlowExecutor(flow);
            // 执行流程
            flowExecutor.execute(targetContext);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            // 包装异常为RuntimeException异常，让外部不用每次调用时都需要catch
            throw new RuntimeException(e);
        }
    }
}
