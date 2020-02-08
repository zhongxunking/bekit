/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 17:55 创建
 */
package org.bekit.flow.locker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bekit.common.method.MethodExecutor;
import org.bekit.flow.engine.FlowContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 特定流程加锁器执行器
 */
@AllArgsConstructor
public class TheFlowLockerExecutor {
    // 加锁的流程
    @Getter
    private final String flow;
    // 特定流程加锁器
    @Getter
    private final Object theFlowLocker;
    // 加锁或解锁执行器Map（key：解锁或解锁的注解类型）
    private final Map<Class<?>, LockOrUnlockExecutor> lockOrUnlockExecutorMap;

    /**
     * 是否包含加解锁类型
     *
     * @param type 加解锁类型
     * @return true 包含；false 不包含
     */
    public boolean contain(Class<?> type) {
        return lockOrUnlockExecutorMap.containsKey(type);
    }

    /**
     * 执行
     *
     * @param type    加解锁类型（@FlowLock、@FlowUnlock、@StateLock、@StateUnlock）
     * @param context 流程上下文
     * @param <T>     目标对象类型
     * @return 如果加锁，则为加锁后的目标对象；否则为null
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public <T> T execute(Class<?> type, FlowContext<T> context) throws Throwable {
        LockOrUnlockExecutor lockOrUnlockExecutor = lockOrUnlockExecutorMap.get(type);
        if (lockOrUnlockExecutor == null) {
            throw new IllegalArgumentException(String.format("流程[%s]的特定流程加锁器不存在@%s方法", flow, type.getSimpleName()));
        }
        return lockOrUnlockExecutor.execute(theFlowLocker, context);
    }

    /**
     * 加锁或解锁执行器
     */
    public static class LockOrUnlockExecutor extends MethodExecutor {
        public LockOrUnlockExecutor(Method lockOrUnlockMethod) {
            super(lockOrUnlockMethod);
        }

        /**
         * 执行
         *
         * @param theFlowLocker 特定流程加锁器
         * @param context       流程上下文
         * @return 如果加锁，则为加锁后的目标对象；否则为null
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public <T> T execute(Object theFlowLocker, FlowContext<T> context) throws Throwable {
            return (T) execute(theFlowLocker, new Object[]{context});
        }
    }
}
