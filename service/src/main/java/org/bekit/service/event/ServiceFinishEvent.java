/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.event;

import org.bekit.service.engine.ServiceContext;

/**
 * 服务完成事件
 */
public class ServiceFinishEvent {
    // 服务名称
    private final String service;
    // 服务上下文
    private final ServiceContext serviceContext;

    public ServiceFinishEvent(String service, ServiceContext serviceContext) {
        this.service = service;
        this.serviceContext = serviceContext;
    }

    public String getService() {
        return service;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }
}
