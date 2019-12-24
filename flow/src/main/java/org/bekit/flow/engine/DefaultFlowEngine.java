/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:49 创建
 */
package org.bekit.flow.engine;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bekit.flow.FlowEngine;
import org.bekit.flow.flow.FlowExecutor;
import org.bekit.flow.flow.FlowRegistrar;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程引擎默认实现类
 */
@AllArgsConstructor
public class DefaultFlowEngine implements FlowEngine {
    // 流程注册器
    private final FlowRegistrar flowRegistrar;

    @Override
    public <T> T execute(String flow, T target) {
        return execute(flow, target, null);
    }

    @Override
    public <T> T execute(String flow, T target, Map<Object, Object> attachment) {
        Assert.notNull(target, "目标对象不能为null");
        attachment = attachment != null ? attachment : new HashMap<>();
        // 构建流程上下文
        FlowContext<T> context = new FlowContext<>(target, attachment);
        // 获取流程执行器
        FlowExecutor flowExecutor = flowRegistrar.getFlow(flow);
        if (flowExecutor == null) {
            throw new IllegalArgumentException(String.format("流程[%s]不存在", flow));
        }
        try {
            // 执行流程
            flowExecutor.execute(context);
        } catch (Throwable e) {
            ExceptionUtils.rethrow(e);
        }

        return context.getTarget();
    }
}
