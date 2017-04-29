/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 12:27 创建
 */
package top.bekit.flow.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import top.bekit.flow.annotation.processor.Execute;
import top.bekit.flow.annotation.processor.Processor;
import top.bekit.flow.engine.TargetContext;
import top.bekit.flow.processor.ProcessorExecutor.ProcessorMethodExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 处理器解析器
 */
public class ProcessorParser {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(ProcessorParser.class);

    /**
     * 解析处理器
     *
     * @param processor 处理器
     * @return 处理器执行器
     */
    public static ProcessorExecutor parseProcessor(Object processor) {
        logger.info("解析处理器：{}", processor);
        // 获取处理器名称
        String processorName = processor.getClass().getAnnotation(Processor.class).name();
        if (StringUtils.isEmpty(processorName)) {
            processorName = ClassUtils.getShortNameAsProperty(processor.getClass());
        }
        // 创建处理器执行器
        ProcessorExecutor processorExecutor = new ProcessorExecutor(processorName, processor);
        for (Method method : processor.getClass().getDeclaredMethods()) {
            for (Class clazz : ProcessorExecutor.PROCESSOR_METHOD_ANNOTATIONS) {
                if (method.isAnnotationPresent(clazz)) {
                    // 设置处理器方法执行器
                    processorExecutor.setMethodExecutor(clazz, parseProcessorMethod(clazz, method));
                    break;
                }
            }
        }
        processorExecutor.validate();

        return processorExecutor;
    }

    /**
     * 解析处理器方法
     */
    private static ProcessorMethodExecutor parseProcessorMethod(Class clazz, Method method) {
        logger.debug("解析处理器方法：{}", method);
        // 校验方法类型
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("处理器方法" + ClassUtils.getQualifiedMethodName(method) + "必须是public类型");
        }
        // 判断是否有入参+校验入参+获取目标对象类型
        Class[] parameterTypes = method.getParameterTypes();
        Class classOfTarget = null;
        if (parameterTypes.length == 1) {
            if (parameterTypes[0] != TargetContext.class) {
                throw new IllegalArgumentException("处理器方法" + ClassUtils.getQualifiedMethodName(method) + "要么没入参，要么入参必须是（TargetContext）");
            }
            // 获取目标对象类型
            ResolvableType resolvableType = ResolvableType.forMethodParameter(method, 0);
            classOfTarget = resolvableType.getGeneric(0).resolve(Object.class);
        } else if (parameterTypes.length != 0) {
            throw new IllegalArgumentException("处理器方法" + ClassUtils.getQualifiedMethodName(method) + "要么没入参，要么入参必须是（TargetContext）");
        }
        // 校验返回类型
        if (clazz != Execute.class && method.getReturnType() != void.class) {
            throw new IllegalArgumentException("非@Execute类型的处理器方法" + ClassUtils.getQualifiedMethodName(method) + "的返回类型必须是void");
        }

        return new ProcessorMethodExecutor(method, classOfTarget);
    }
}
