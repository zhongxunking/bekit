/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-31 17:39 创建
 */
package top.bekit.flow.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ClassUtils;
import top.bekit.flow.annotation.transaction.InsertTarget;
import top.bekit.flow.annotation.transaction.LockTarget;
import top.bekit.flow.engine.TargetContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 流程事务执行器
 */
public class FlowTxExecutor {
    /**
     * 流程事务方法注解
     */
    public static final Class[] FLOW_TX_METHOD_ANNOTATIONS = {LockTarget.class, InsertTarget.class};
    // 流程事务定义（传播行为是REQUIRES_NEW，即每次都开启一个新事务）
    private static final TransactionDefinition FLOW_TX_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    // 对应的流程名称
    private String flow;
    // 流程事务
    private Object flowTx;
    // 流程事务方法执行器Map（key：流程事务方法注解的Class）
    private Map<Class, FlowTxMethodExecutor> flowTxMethodExecutorMap = new HashMap<>();
    // 事务管理器
    private PlatformTransactionManager txManager;
    // 事务持有器
    private ThreadLocal<TransactionStatus> txStatusHolder = new ThreadLocal<>();

    public FlowTxExecutor(String flow, Object flowTx, PlatformTransactionManager txManager) {
        this.flow = flow;
        this.flowTx = flowTx;
        this.txManager = txManager;
    }

    /**
     * 创建事务
     *
     * @throws IllegalStateException 如果已存在事务
     */
    public void createTx() {
        if (txStatusHolder.get() != null) {
            throw new IllegalStateException("流程" + flow + "事务已存在，不能同时创建多个事务");
        }
        txStatusHolder.set(txManager.getTransaction(FLOW_TX_DEFINITION));
    }

    /**
     * 提交事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void commitTx() {
        if (txStatusHolder.get() == null) {
            throw new IllegalStateException("流程" + flow + "事务不存在，无法提交");
        }
        txManager.commit(txStatusHolder.get());
        txStatusHolder.remove();
    }

    /**
     * 回滚事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void rollbackTx() {
        if (txStatusHolder.get() == null) {
            throw new IllegalStateException("流程" + flow + "事务不存在，无法回滚");
        }
        txManager.rollback(txStatusHolder.get());
        txStatusHolder.remove();
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
     * 校验流程事务执行器有效性
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (flow == null || flowTx == null || txManager == null) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "内部要素不全");
        }
        if (!flowTxMethodExecutorMap.containsKey(LockTarget.class)) {
            throw new IllegalStateException("流程事务" + ClassUtils.getShortName(flowTx.getClass()) + "不存在@LockTarget类型方法");
        }
    }

    /**
     * 获取对应流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 流程事务方法执行器
     */
    public static class FlowTxMethodExecutor {
        // 目标方法
        private Method targetMethod;

        public FlowTxMethodExecutor(Method targetMethod) {
            this.targetMethod = targetMethod;
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
            try {
                return targetMethod.invoke(flowTx, targetContext);
            } catch (InvocationTargetException e) {
                // 抛出原始异常
                throw e.getTargetException();
            }
        }
    }
}
