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
import org.bekit.service.annotation.service.ServiceAfter;
import org.bekit.service.annotation.service.ServiceBefore;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;

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
    private final Map<Class, ServicePhaseExecutor> phaseExecutorMap;
    // 事务执行器
    private final TxExecutor txExecutor;

    /**
     * 执行服务
     *
     * @param serviceContext 服务上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void execute(ServiceContext serviceContext) throws Throwable {
        // 执行服务前置阶段（如果存在）
        if (phaseExecutorMap.containsKey(ServiceBefore.class)) {
            phaseExecutorMap.get(ServiceBefore.class).execute(service, serviceContext);
        }
        // 执行服务执行阶段
        executeServiceExecute(serviceContext);
        // 执行服务后置阶段（如果存在）
        if (phaseExecutorMap.containsKey(ServiceAfter.class)) {
            phaseExecutorMap.get(ServiceAfter.class).execute(service, serviceContext);
        }
    }

    // 执行服务执行阶段
    private void executeServiceExecute(ServiceContext serviceContext) throws Throwable {
        if (txExecutor != null) {
            // 开启事务
            txExecutor.createTx();
        }
        try {
            phaseExecutorMap.get(ServiceExecute.class).execute(service, serviceContext);
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
    public Class getOrderClass() {
        return phaseExecutorMap.get(ServiceExecute.class).getOrderClass();
    }

    /**
     * 获取Result的真实类型
     */
    public Class getResultClass() {
        return phaseExecutorMap.get(ServiceExecute.class).getResultClass();
    }

    /**
     * 服务阶段执行器
     */
    @Getter
    public static class ServicePhaseExecutor extends MethodExecutor {
        // ServiceContext泛型O的真实类型
        private final Class orderClass;
        // ServiceContext泛型R的真实类型
        private final Class resultClass;

        public ServicePhaseExecutor(Method targetMethod, Class orderClass, Class resultClass) {
            super(targetMethod);
            this.orderClass = orderClass;
            this.resultClass = resultClass;
        }

        /**
         * 执行服务阶段
         *
         * @param service        服务
         * @param serviceContext 服务上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object service, ServiceContext serviceContext) throws Throwable {
            execute(service, new Object[]{serviceContext});
        }
    }
}
