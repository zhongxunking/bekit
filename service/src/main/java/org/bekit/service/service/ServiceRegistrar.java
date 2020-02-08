/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-25 23:41 创建
 */
package org.bekit.service.service;

import org.bekit.common.registrar.AbstractRegistrar;

/**
 * 服务注册器
 */
public class ServiceRegistrar extends AbstractRegistrar<String, ServiceExecutor> {
    public ServiceRegistrar() {
        super(ServiceExecutor::getServiceName);
    }
}
