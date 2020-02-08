/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 17:45 创建
 */
package org.bekit.flow.mapper;

import org.bekit.common.registrar.AbstractRegistrar;

/**
 * 特定流程映射器注册器
 */
public class TheFlowMapperRegistrar extends AbstractRegistrar<String, TheFlowMapperExecutor> {
    public TheFlowMapperRegistrar() {
        super(TheFlowMapperExecutor::getFlow);
    }
}
