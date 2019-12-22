/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 16:51 创建
 */
package org.bekit.flow.mapper;

import lombok.Getter;
import org.bekit.common.method.MethodExecutor;
import org.bekit.flow.engine.FlowContext;

import java.lang.reflect.Method;

/**
 * 特定流程映射器执行器
 */
@Getter
public class TheFlowMapperExecutor extends MethodExecutor {
    // 映射的流程
    private final String flow;
    // 特定流程映射器
    private final Object theFlowMapper;

    public TheFlowMapperExecutor(String flow, Object theFlowMapper, Method mappingNodeMethod) {
        super(mappingNodeMethod);
        this.flow = flow;
        this.theFlowMapper = theFlowMapper;
    }

    /**
     * 执行
     *
     * @param context 流程上下文
     * @return 映射出的节点名称
     * @throws Throwable 执行过程中发生任何异常都后会往外抛
     */
    public String execute(FlowContext<?> context) throws Throwable {
        return (String) execute(theFlowMapper, new Object[]{context});
    }
}
