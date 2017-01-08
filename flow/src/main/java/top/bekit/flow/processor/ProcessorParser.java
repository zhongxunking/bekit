/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 12:27 创建
 */
package top.bekit.flow.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;
import top.bekit.flow.annotation.processor.Execute;
import top.bekit.flow.annotation.processor.Processor;
import top.bekit.flow.engine.TargetContext;
import top.bekit.flow.processor.ProcessorExecutor.ProcessorMethodExecutor;

import java.lang.reflect.Method;

/**
 * 处理器解析器
 */
public class ProcessorParser {

    /**
     * 解析处理器
     *
     * @param processor 处理器
     * @return 处理器执行器
     */
    public static ProcessorExecutor parseProcessor(Object processor) {
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
        boolean hasParameter;
        // 判断是否有入参+校验入参
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            hasParameter = false;
        } else if (parameterTypes.length == 1) {
            if (parameterTypes[0] != TargetContext.class) {
                throw new IllegalArgumentException("处理器方法" + ClassUtils.getQualifiedMethodName(method) + "的入参必须是TargetContext类型");
            }
            hasParameter = true;
        } else {
            throw new IllegalArgumentException("处理器方法" + ClassUtils.getQualifiedMethodName(method) + "最多只能有一个入参且必须是TargetContext类型");
        }
        // 校验返回类型
        if (clazz != Execute.class && method.getReturnType() != void.class) {
            throw new IllegalArgumentException("非@Execute类型的处理器方法" + ClassUtils.getQualifiedMethodName(method) + "的返回类型必须是void");
        }

        return new ProcessorMethodExecutor(method, hasParameter);
    }
}
