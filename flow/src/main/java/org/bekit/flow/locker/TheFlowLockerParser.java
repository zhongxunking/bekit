/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 18:36 创建
 */
package org.bekit.flow.locker;

import lombok.extern.slf4j.Slf4j;
import org.bekit.flow.annotation.locker.*;
import org.bekit.flow.engine.FlowContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 特定流程加锁器解析器
 */
@Slf4j
public final class TheFlowLockerParser {
    // 加解锁类型
    private static final Class<? extends Annotation>[] LOCK_UNLOCK_TYPES = new Class[]{FlowLock.class, FlowUnlock.class, StateLock.class, StateUnlock.class};

    /**
     * 解析特定流程加锁器
     *
     * @param theFlowLocker 特定流程加锁器
     * @return 特定流程加锁器执行器
     */
    public static TheFlowLockerExecutor parseTheFlowLocker(Object theFlowLocker) {
        // 获取目标class（应对AOP代理情况）
        Class<?> theFlowLockerClass = AopUtils.getTargetClass(theFlowLocker);
        log.debug("解析特定流程加锁器：{}", theFlowLockerClass);
        // 解析加锁的流程
        TheFlowLocker theFlowLockerAnnotation = AnnotatedElementUtils.findMergedAnnotation(theFlowLockerClass, TheFlowLocker.class);
        // 解析出所有加解锁
        Map<Class<?>, TheFlowLockerExecutor.LockOrUnlockExecutor> lockOrUnlockExecutorMap = parseToLockOrUnlockExecutors(theFlowLockerClass);

        return new TheFlowLockerExecutor(theFlowLockerAnnotation.flow(), theFlowLocker, lockOrUnlockExecutorMap);
    }

    // 解析出所有加解锁
    private static Map<Class<?>, TheFlowLockerExecutor.LockOrUnlockExecutor> parseToLockOrUnlockExecutors(Class<?> theFlowLockerClass) {
        Map<Class<?>, TheFlowLockerExecutor.LockOrUnlockExecutor> executorMap = new HashMap<>();
        // 解析
        ReflectionUtils.doWithLocalMethods(theFlowLockerClass, method -> {
            for (Class<? extends Annotation> type : LOCK_UNLOCK_TYPES) {
                if (AnnotatedElementUtils.findMergedAnnotation(method, type) != null) {
                    executorMap.put(type, parseLockOrUnlock(type, method));
                }
            }
        });

        return executorMap;
    }

    // 解析加解锁
    private static TheFlowLockerExecutor.LockOrUnlockExecutor parseLockOrUnlock(Class<?> type, Method lockOrUnlockMethod) {
        log.debug("解析加解锁方法{}", lockOrUnlockMethod);
        // 校验方法类型
        Assert.isTrue(Modifier.isPublic(lockOrUnlockMethod.getModifiers()), String.format("@%s方法[%s]必须是public类型", type.getSimpleName(), lockOrUnlockMethod));
        // 校验入参类型
        Class<?>[] parameterTypes = lockOrUnlockMethod.getParameterTypes();
        if (parameterTypes.length != 1 || parameterTypes[0] != FlowContext.class) {
            throw new IllegalArgumentException(String.format("@%s方法[%s]入参类型必须是(FlowContext<T>)", type.getSimpleName(), lockOrUnlockMethod));
        }
        ResolvableType resolvableType = ResolvableType.forMethodParameter(lockOrUnlockMethod, 0);
        Class<?> targetType = resolvableType.getGeneric(0).resolve(Object.class);
        // 校验返回类型
        if (type == FlowLock.class || type == StateLock.class) {
            Assert.isTrue(lockOrUnlockMethod.getReturnType() == targetType, String.format("@%s方法[%s]返回类型与FlowContext的目标对象类型必须一致", type.getSimpleName(), lockOrUnlockMethod));
        } else {
            Assert.isTrue(lockOrUnlockMethod.getReturnType() == void.class, String.format("@%s方法[%s]返回类型必须为void", type.getSimpleName(), lockOrUnlockMethod));
        }

        return new TheFlowLockerExecutor.LockOrUnlockExecutor(lockOrUnlockMethod);
    }
}
