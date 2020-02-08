/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:26 创建
 */
package org.bekit.flow.flow;

import org.bekit.common.registrar.AbstractRegistrar;

/**
 * 流程注册器
 */
public class FlowRegistrar extends AbstractRegistrar<String, FlowExecutor> {
    public FlowRegistrar() {
        super(FlowExecutor::getFlowName);
    }
}
