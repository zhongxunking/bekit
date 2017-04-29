/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-31 17:39 创建
 */
package top.bekit.flow.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;
import top.bekit.common.method.MethodExecutor;
import top.bekit.common.transaction.TxExecutor;
import top.bekit.flow.annotation.transaction.InsertTarget;
import top.bekit.flow.annotation.transaction.LockTarget;
import top.bekit.flow.engine.TargetContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程事务执行器
 */
public class FlowTxExecutor extends TxExecutor {
    /**
     * 流程事务方法注解
     */
    public static final Class[] FLOW_TX_METHOD_ANNOTATIONS = {LockTarget.class, InsertTarget.class};

    // 对应的流程名称
    private String flow;
    // 流程事务
    private Object flowTx;
    // 流程事务方法执行器Map（key：流程事务方法注解的Class）
    private Map<Class, FlowTxMethodExecutor> flowTxMethodExecutorMap = new HashMap<>();

    public FlowTxExecutor(String flow, Object flowTx, PlatformTransactionManager txManager) {
        super(txManager);
        this.flow = flow;
        this.flowTx = flowTx;
    }

    /**
     * 锁住目标对象
     *
     * @param targetContext 目标上下文
     * @throws IllegalStateException 如果不存在@LockTarget类型方法
     * @throws Throwable             执行过程中发生任何异常都会往外抛
     */
    public void lockTarget(TargetContext targetContext) throws Throwable {
        // 执行@LockTarget类型方法执行器
        FlowTxMethodExecutor methodExecutor = flowTxMethodExecutorMap.get(LockTarget.class);
        if (methodExecutor == null) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "不存在@LockTarget类型方法");
        }
        targetContext.refreshTarget(methodExecutor.execute(flowTx, targetContext));
    }

    /**
     * 创建新事务插入目标对象并提交事务
     *
     * @param targetContext 目标上下文
     * @throws IllegalStateException 如果不存在@InsertTarget类型方法
     * @throws Throwable             执行过程中发生任何异常都会往外抛
     */
    public void insertTarget(TargetContext targetContext) throws Throwable {
        // 创建事务
        createTx();
        try {
            // 执行@InsertTarget类型方法执行器
            FlowTxMethodExecutor methodExecutor = flowTxMethodExecutorMap.get(InsertTarget.class);
            if (methodExecutor == null) {
                throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "不存在@InsertTarget类型方法");
            }
            targetContext.refreshTarget(methodExecutor.execute(flowTx, targetContext));
            // 提交事务
            commitTx();
        } catch (Throwable e) {
            // 回滚事务
            rollbackTx();
            throw e;
        }
    }

    /**
     * 设置流程事务方法执行器
     *
     * @param clazz          流程事务方法注解的Class
     * @param methodExecutor 流程事务方法执行器
     * @throws IllegalArgumentException 如果入参clazz不是流程事务方法注解
     * @throws IllegalStateException    如果已存在该类型的方法处理器
     */
    public void setMethodExecutor(Class clazz, FlowTxMethodExecutor methodExecutor) {
        if (!Arrays.asList(FLOW_TX_METHOD_ANNOTATIONS).contains(clazz)) {
            throw new IllegalArgumentException(ClassUtils.getShortName(clazz) + "不是流程事务方法注解");
        }
        if (flowTxMethodExecutorMap.containsKey(clazz)) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "存在多个@" + ClassUtils.getShortName(clazz) + "类型的方法");
        }
        flowTxMethodExecutorMap.put(clazz, methodExecutor);
    }

    /**
     * 获取对应流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 获取目标对象类型
     */
    public Class getClassOfTarget() {
        return flowTxMethodExecutorMap.get(LockTarget.class).getClassOfTarget();
    }

    /**
     * 校验流程事务执行器有效性
     *
     * @throws IllegalStateException 如果校验不通过
     */
    @Override
    public void validate() {
        super.validate();
        if (flow == null || flowTx == null) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "内部要素不全");
        }
        if (!flowTxMethodExecutorMap.containsKey(LockTarget.class)) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "不存在@LockTarget类型方法");
        }
        // 校验流程事务内目标对象类型是否统一
        for (FlowTxMethodExecutor methodExecutor : flowTxMethodExecutorMap.values()) {
            if (methodExecutor.getClassOfTarget() != getClassOfTarget()) {
                throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "内目标对象类型不统一");
            }
        }
    }

    /**
     * 流程事务方法执行器
     */
    public static class FlowTxMethodExecutor extends MethodExecutor {
        // 目标对象类型
        private Class classOfTarget;

        public FlowTxMethodExecutor(Method targetMethod, Class classOfTarget) {
            super(targetMethod);
            this.classOfTarget = classOfTarget;
        }

        /**
         * 执行流程事务方法
         *
         * @param flowTx        流程事务
         * @param targetContext 目标上下文
         * @return 该操作后的目标对象
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public Object execute(Object flowTx, TargetContext targetContext) throws Throwable {
            return execute(flowTx, new Object[]{targetContext});
        }

        /**
         * 获取目标对象类型
         */
        public Class getClassOfTarget() {
            return classOfTarget;
        }
    }
}
