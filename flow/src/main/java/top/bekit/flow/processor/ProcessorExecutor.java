/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 11:35 创建
 */
package top.bekit.flow.processor;

import org.springframework.util.ClassUtils;
import top.bekit.common.method.MethodExecutor;
import top.bekit.flow.annotation.processor.*;
import top.bekit.flow.annotation.processor.Error;
import top.bekit.flow.engine.TargetContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理器执行器
 */
public class ProcessorExecutor {
    /**
     * 处理器方法注解
     */
    public static final Class[] PROCESSOR_METHOD_ANNOTATIONS = {Before.class, Execute.class, After.class, End.class, Error.class};

    // 处理器名称
    private String processorName;
    // 处理器
    private Object processor;
    // 处理器方法执行器Map（key：处理器方法注解的Class）
    private Map<Class, ProcessorMethodExecutor> methodExecutorMap = new HashMap<>();

    public ProcessorExecutor(String processorName, Object processor) {
        this.processorName = processorName;
        this.processor = processor;
    }

    /**
     * 执行处理器
     * （顺序：@Before、@Execute、@After、@End；如果执行@Before、@Execute、@After发生异常，则会在执行@End之前执行@Error）
     *
     * @param targetContext 目标上下文
     * @return Execute类型方法返回的结果
     * @throws Throwable 执行过程中发生任何异常都后会往外抛
     */
    public Object execute(TargetContext targetContext) throws Throwable {
        try {
            executeMethod(Before.class, targetContext);
            Object result = executeMethod(Execute.class, targetContext);
            executeMethod(After.class, targetContext);
            return result;
        } catch (Throwable e) {
            executeMethod(Error.class, targetContext);
            throw e;
        } finally {
            executeMethod(End.class, targetContext);
        }
    }

    // 执行处理器方法（对于不存在对应的方法，则忽略并返回null）
    private Object executeMethod(Class clazz, TargetContext targetContext) throws Throwable {
        ProcessorMethodExecutor methodExecutor = methodExecutorMap.get(clazz);
        if (methodExecutor == null) {
            return null;
        }
        return methodExecutor.execute(processor, targetContext);
    }

    /**
     * 设置处理器方法执行器
     *
     * @param clazz          处理器方法注解
     * @param methodExecutor 方法执行器
     * @throws IllegalArgumentException 如果annotationClass不是处理器方法注解
     * @throws IllegalStateException    如果已存在该类型的处理器方法执行器
     */
    public void setMethodExecutor(Class clazz, ProcessorMethodExecutor methodExecutor) {
        if (!Arrays.asList(PROCESSOR_METHOD_ANNOTATIONS).contains(clazz)) {
            throw new IllegalArgumentException(ClassUtils.getShortName(clazz) + "不是处理器方法注解");
        }
        if (methodExecutorMap.containsKey(clazz)) {
            throw new IllegalStateException("处理器" + processorName + "存在多个@" + ClassUtils.getShortName(clazz) + "类型的方法");
        }
        methodExecutorMap.put(clazz, methodExecutor);
    }

    /**
     * 校验处理器执行器是否有效
     *
     * @throws IllegalStateException 校验不通过
     */
    public void validate() {
        if (processorName == null || processor == null) {
            throw new IllegalStateException("处理器" + processorName + "内部要素不全");
        }
        if (!methodExecutorMap.containsKey(Execute.class)) {
            throw new IllegalStateException("处理器" + processorName + "不存在@Execute类型的处理器方法");
        }
    }

    /**
     * 获取返回类型
     *
     * @throws IllegalStateException 如果不存在@Execute类型的处理器方法
     */
    public Class getReturnType() {
        ProcessorMethodExecutor methodExecutor = methodExecutorMap.get(Execute.class);
        if (methodExecutor == null) {
            throw new IllegalStateException("处理器" + processorName + "不存在@Execute类型的处理器方法，无法获取返回类型");
        }
        return methodExecutor.getReturnType();
    }

    /**
     * 获取处理器名称
     */
    public String getProcessorName() {
        return processorName;
    }

    /**
     * 处理器方法执行器
     */
    public static class ProcessorMethodExecutor extends MethodExecutor {
        // 是否有入参
        private boolean hasParameter;

        public ProcessorMethodExecutor(Method targetMethod) {
            super(targetMethod);
            this.hasParameter = getParameterTypes().length > 0;
        }

        /**
         * 执行处理器方法
         *
         * @param processor     处理器
         * @param targetContext 目标上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public Object execute(Object processor, TargetContext targetContext) throws Throwable {
            if (hasParameter) {
                return execute(processor, new Object[]{targetContext});
            } else {
                return execute(processor, (Object[]) null);
            }
        }
    }
}
