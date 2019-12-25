/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bekit.common.method.MethodExecutor;
import org.bekit.common.transaction.TxExecutor;
import org.bekit.event.EventPublisher;
import org.bekit.service.annotation.service.ServiceAfter;
import org.bekit.service.annotation.service.ServiceBefore;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.bekit.service.event.ServiceApplyEvent;
import org.bekit.service.event.ServiceExceptionEvent;
import org.bekit.service.event.ServiceFinishEvent;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 服务执行器
 */
@AllArgsConstructor
public class ServiceExecutor {
    // 服务名称
    @Getter
    private final String serviceName;
    // 服务
    @Getter
    private final Object service;
    // 服务阶段执行器Map（key：服务阶段注解的Class）
    private final Map<Class<?>, ServicePhaseExecutor> phaseExecutorMap;
    // 事件发布器
    private final EventPublisher eventPublisher;
    // 事务执行器
    private final TxExecutor txExecutor;

    /**
     * 执行
     *
     * @param context 服务上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void execute(ServiceContext context) {
        try {
            // 发布服务申请事件
            eventPublisher.publish(new ServiceApplyEvent(serviceName, context));
            // 执行所有服务阶段
            executePhases(context);
        } catch (Throwable e) {
            // 发布服务异常事件
            eventPublisher.publish(new ServiceExceptionEvent(serviceName, e, context));
        } finally {
            // 发布服务结束事件
            eventPublisher.publish(new ServiceFinishEvent(serviceName, context));
        }
    }

    // 执行所有服务阶段
    private void executePhases(ServiceContext context) throws Throwable {
        // 执行服务前置阶段（如果存在）
        if (phaseExecutorMap.containsKey(ServiceBefore.class)) {
            phaseExecutorMap.get(ServiceBefore.class).execute(service, context);
        }
        // 执行服务执行阶段
        executeServiceExecute(context);
        // 执行服务后置阶段（如果存在）
        if (phaseExecutorMap.containsKey(ServiceAfter.class)) {
            phaseExecutorMap.get(ServiceAfter.class).execute(service, context);
        }
    }

    // 执行服务执行阶段
    private void executeServiceExecute(ServiceContext context) throws Throwable {
        if (txExecutor != null) {
            // 开启事务
            txExecutor.createTx();
        }
        try {
            phaseExecutorMap.get(ServiceExecute.class).execute(service, context);
            if (txExecutor != null) {
                // 提交事务
                txExecutor.commitTx();
            }
        } catch (Throwable e) {
            if (txExecutor != null) {
                // 回滚事务
                txExecutor.rollbackTx();
            }
            throw e;
        }
    }

    /**
     * 获取Order的真实类型
     */
    public Class<?> getOrderClass() {
        return phaseExecutorMap.get(ServiceExecute.class).getOrderClass();
    }

    /**
     * 获取Result的真实类型
     */
    public Class<?> getResultClass() {
        return phaseExecutorMap.get(ServiceExecute.class).getResultClass();
    }

    /**
     * 服务阶段执行器
     */
    @Getter
    public static class ServicePhaseExecutor extends MethodExecutor {
        // ServiceContext泛型O的真实类型
        private final Class<?> orderClass;
        // ServiceContext泛型R的真实类型
        private final Class<?> resultClass;

        public ServicePhaseExecutor(Method servicePhaseMethod, Class<?> orderClass, Class<?> resultClass) {
            super(servicePhaseMethod);
            this.orderClass = orderClass;
            this.resultClass = resultClass;
        }

        /**
         * 执行服务阶段
         *
         * @param service 服务
         * @param context 服务上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object service, ServiceContext context) throws Throwable {
            execute(service, new Object[]{context});
        }
    }
}
