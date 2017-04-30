/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:49 创建
 */
package top.bekit.flow.engine;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
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
    public <T> T start(String flow, T target) {
        return start(flow, target, null);
    }

    @Override
    public <T> T start(String flow, T target, Map<Object, Object> attachment) {
        // 构造目标上下文
        TargetContext<T> targetContext = new TargetContext(target, attachment);
        // 执行流程
        executeFlow(flow, targetContext);

        return targetContext.getTarget();
    }

    @Override
    public <T> T insertTargetAndStart(String flow, T target, Map<Object, Object> attachment) {
        // 构造目标上下文
        TargetContext<T> targetContext = new TargetContext(target, attachment);
        try {
            //以新事务插入目标对象到数据库并提交
            flowTxHolder.getRequiredFlowTxExecutor(flow).insertTarget(targetContext);
            // 执行流程
            executeFlow(flow, targetContext);
        } catch (Throwable e) {
            // 非运行时异常包装成UndeclaredThrowableException异常，让外部不用每次调用时都需要catch
            ExceptionUtils.wrapAndThrow(e);
        }
        return targetContext.getTarget();
    }

    // 执行流程
    private void executeFlow(String flow, TargetContext targetContext) {
        try {
            // 获取流程执行器
            FlowExecutor flowExecutor = flowHolder.getRequiredFlowExecutor(flow);
            // 校验目标对象类型
            checkClassOfTarget(targetContext.getTarget(), flowExecutor);
            // 执行流程
            flowExecutor.execute(targetContext);
        } catch (Throwable e) {
            // 非运行时异常包装成UndeclaredThrowableException异常，让外部不用每次调用时都需要catch
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    // 校验目标对象类型
    private void checkClassOfTarget(Object target, FlowExecutor flowExecutor) {
        if (!flowExecutor.getClassOfTarget().isAssignableFrom(target.getClass())) {
            throw new IllegalArgumentException(String.format("传入的目标对象的类型[%s]和流程%s期望的类型[%s]不匹配", ClassUtils.getShortName(target.getClass()), flowExecutor.getFlowName(), ClassUtils.getShortName(flowExecutor.getClassOfTarget())));
        }
    }
}
