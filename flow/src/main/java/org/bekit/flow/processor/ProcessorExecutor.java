/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 11:35 创建
 */
package org.bekit.flow.processor;

import lombok.Getter;
import org.bekit.common.method.MethodExecutor;
import org.bekit.flow.engine.FlowContext;

import java.lang.reflect.Method;

/**
 * 处理器执行器
 */
@Getter
public class ProcessorExecutor extends MethodExecutor {
    // 处理器名称
    private final String processorName;
    // 处理器
    private final Object processor;

    public ProcessorExecutor(String processorName, Object processor, Method executeMethod) {
        super(executeMethod);
        this.processorName = processorName;
        this.processor = processor;
    }

    /**
     * 执行处理器
     *
     * @param context 流程上下文
     * @return Execute类型方法返回的结果
     * @throws Throwable 执行过程中发生任何异常都后会往外抛
     */
    public Object execute(FlowContext<?> context) throws Throwable {
        return execute(processor, new Object[]{context});
    }
}
