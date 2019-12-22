/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 12:27 创建
 */
package org.bekit.flow.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bekit.flow.annotation.processor.Processor;
import org.bekit.flow.annotation.processor.ProcessorExecute;
import org.bekit.flow.engine.FlowContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 处理器解析器
 */
@Slf4j
public final class ProcessorParser {
    /**
     * 解析处理器
     *
     * @param processor 处理器
     * @return 处理器执行器
     */
    public static ProcessorExecutor parseProcessor(Object processor) {
        // 获取目标class（应对AOP代理情况）
        Class<?> processorClass = AopUtils.getTargetClass(processor);
        log.debug("解析处理器：{}", processorClass);
        // 获取处理器名称
        Processor processorAnnotation = AnnotatedElementUtils.findMergedAnnotation(processorClass, Processor.class);
        String processorName = processorAnnotation.name();
        if (StringUtils.isEmpty(processorName)) {
            processorName = ClassUtils.getShortNameAsProperty(processorClass);
        }
        // 解析处理器方法
        Method executeMethod = parseExecuteMethod(processorClass);

        return new ProcessorExecutor(processorName, processor, executeMethod);
    }

    // 解析@ProcessorExecute方法
    private static Method parseExecuteMethod(Class<?> processorClass) {
        for (Method method : processorClass.getDeclaredMethods()) {
            if (AnnotatedElementUtils.findMergedAnnotation(method, ProcessorExecute.class) == null) {
                continue;
            }
            // 校验
            Assert.isTrue(Modifier.isPublic(method.getModifiers()), String.format("@ProcessorExecute方法[%s]必须是public类型", method));
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 || parameterTypes[0] != FlowContext.class) {
                throw new IllegalArgumentException(String.format("@ProcessorExecute方法[%s]的入参必须是(FlowContext<T> context)", method));
            }

            return method;
        }
        throw new IllegalArgumentException(String.format("处理器[%s]不存在@ProcessorExecute方法", processorClass));
    }
}
