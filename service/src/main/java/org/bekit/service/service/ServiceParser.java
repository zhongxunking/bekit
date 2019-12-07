/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bekit.common.transaction.TransactionManager;
import org.bekit.common.transaction.TxExecutor;
import org.bekit.service.annotation.service.Service;
import org.bekit.service.annotation.service.ServiceAfter;
import org.bekit.service.annotation.service.ServiceBefore;
import org.bekit.service.annotation.service.ServiceExecute;
import org.bekit.service.engine.ServiceContext;
import org.bekit.service.service.ServiceExecutor.ServicePhaseExecutor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务解析器
 */
@Slf4j
public final class ServiceParser {
    // 服务阶段注解
    private static final Class<? extends Annotation>[] SERVICE_PHASE_ANNOTATIONS = new Class[]{ServiceBefore.class, ServiceExecute.class, ServiceAfter.class};

    /**
     * 解析服务
     *
     * @param service            服务
     * @param transactionManager 事务管理器
     * @return 服务执行器
     */
    public static ServiceExecutor parseService(Object service, TransactionManager transactionManager) {
        // 获取目标class（应对AOP代理情况）
        Class<?> serviceClass = AopUtils.getTargetClass(service);
        log.debug("解析服务：{}", serviceClass);
        Service serviceAnnotation = AnnotatedElementUtils.findMergedAnnotation(serviceClass, Service.class);
        // 获取服务名称
        String serviceName = serviceAnnotation.name();
        if (StringUtils.isEmpty(serviceName)) {
            serviceName = ClassUtils.getShortNameAsProperty(serviceClass);
        }
        // 计算事务执行器
        TxExecutor txExecutor = null;
        if (serviceAnnotation.enableTx()) {
            txExecutor = new TxExecutor(transactionManager, TransactionManager.TransactionType.REQUIRED);
        }
        // 解析所有服务阶段
        Map<Class<?>, ServicePhaseExecutor> phaseExecutorMap = parsePhaseExecutors(serviceClass);

        return new ServiceExecutor(serviceName, service, phaseExecutorMap, txExecutor);
    }

    // 解析所有服务阶段
    private static Map<Class<?>, ServicePhaseExecutor> parsePhaseExecutors(Class serviceClass) {
        Map<Class<?>, ServicePhaseExecutor> map = new HashMap<>();
        // 解析
        ReflectionUtils.doWithLocalMethods(serviceClass, method -> {
            for (Class<? extends Annotation> annotationClass : SERVICE_PHASE_ANNOTATIONS) {
                Annotation annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationClass);
                if (annotation != null) {
                    map.put(annotationClass, parseServicePhase(method));
                }
            }
        });
        // 校验
        Assert.isTrue(map.containsKey(ServiceExecute.class), String.format("服务[%s]缺少@ServiceExecute类型方法", serviceClass));
        Class<?> orderClass = map.get(ServiceExecute.class).getOrderClass();
        Class<?> resultClass = map.get(ServiceExecute.class).getResultClass();
        Assert.isTrue(ClassUtils.hasConstructor(resultClass), String.format("@ServiceExecute服务方法[%s]的参数ServiceContext的泛型[%s]必须得有默认构造函数", map.get(ServiceExecute.class).getMethod(), resultClass));
        map.forEach((annotationClass, phaseExecutor) -> {
            Assert.isAssignable(phaseExecutor.getOrderClass(), orderClass, String.format("服务[%s]内的ServiceContext的泛型类型不统一", serviceClass));
            Assert.isAssignable(phaseExecutor.getResultClass(), resultClass, String.format("服务[%s]内的ServiceContext的泛型类型不统一", serviceClass));
        });

        return map;
    }

    // 解析服务阶段方法
    private static ServicePhaseExecutor parseServicePhase(Method servicePhaseMethod) {
        log.debug("解析服务方法：{}", servicePhaseMethod);
        // 校验方法类型、返回类型
        Assert.isTrue(Modifier.isPublic(servicePhaseMethod.getModifiers()), String.format("服务方法[%s]必须是public类型", servicePhaseMethod));
        Assert.isTrue(servicePhaseMethod.getReturnType() == void.class, String.format("服务方法[%s]的返回类型必须是void", servicePhaseMethod));
        // 校验入参
        Class<?>[] parameterTypes = servicePhaseMethod.getParameterTypes();
        if (parameterTypes.length != 1 || parameterTypes[0] != ServiceContext.class) {
            throw new IllegalArgumentException(String.format("服务方法[%s]的入参必须是(ServiceContext<O,R> context)", servicePhaseMethod));
        }
        // 获取ServiceContext中泛型O、R的真实类型
        ResolvableType resolvableType = ResolvableType.forMethodParameter(servicePhaseMethod, 0);
        Class<?> orderClass = resolvableType.getGeneric(0).resolve(Object.class);
        Class<?> resultClass = resolvableType.getGeneric(1).resolve(Object.class);

        return new ServicePhaseExecutor(servicePhaseMethod, orderClass, resultClass);
    }
}
