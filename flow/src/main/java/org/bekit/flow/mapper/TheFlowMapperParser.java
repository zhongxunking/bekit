/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 17:04 创建
 */
package org.bekit.flow.mapper;

import lombok.extern.slf4j.Slf4j;
import org.bekit.flow.annotation.mapper.MappingNode;
import org.bekit.flow.annotation.mapper.TheFlowMapper;
import org.bekit.flow.engine.FlowContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 特定流程映射器解析器
 */
@Slf4j
public final class TheFlowMapperParser {
    /**
     * 解析特定流程映射器
     *
     * @param theFlowMapper 特定流程映射器
     * @return 特定流程映射器执行器
     */
    public static TheFlowMapperExecutor parseTheFlowMapper(Object theFlowMapper) {
        // 获取目标class（应对AOP代理情况）
        Class<?> theFlowMapperClass = AopUtils.getTargetClass(theFlowMapper);
        log.debug("解析特定流程映射器：{}", theFlowMapperClass);
        // 获取映射的流程
        TheFlowMapper theFlowMapperAnnotation = AnnotatedElementUtils.findMergedAnnotation(theFlowMapperClass, TheFlowMapper.class);
        // 解析处理器方法
        Method mappingNodeMethod = parseToMappingNodeMethod(theFlowMapperClass);

        return new TheFlowMapperExecutor(theFlowMapperAnnotation.flow(), theFlowMapper, mappingNodeMethod);
    }

    // 解析出@MappingNode方法
    private static Method parseToMappingNodeMethod(Class<?> theFlowMapperClass) {
        for (Method method : theFlowMapperClass.getDeclaredMethods()) {
            if (AnnotatedElementUtils.findMergedAnnotation(method, MappingNode.class) == null) {
                continue;
            }
            // 校验方法类型、返回类型
            Assert.isTrue(Modifier.isPublic(method.getModifiers()), String.format("@MappingNode方法[%s]必须是public类型", method));
            Assert.isTrue(method.getReturnType() == String.class, String.format("@MappingNode方法[%s]返回类型必须是String", method));
            // 校验入参类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 || parameterTypes[0] != FlowContext.class) {
                throw new IllegalArgumentException(String.format("@ProcessorExecute方法[%s]的入参必须是(FlowContext<T> context)", method));
            }

            return method;
        }
        throw new IllegalArgumentException(String.format("特定流程映射器[%s]不存在@MappingNode法", theFlowMapperClass));
    }
}
