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
 * 服务异常事件
 */
public class ServiceExceptionEvent {
    // 服务名称
    private final String service;
    // 服务上下文
    private final ServiceContext serviceContext;
    // 发生的异常
    private final Throwable throwable;

    public ServiceExceptionEvent(String service, ServiceContext serviceContext, Throwable throwable) {
        this.service = service;
        this.serviceContext = serviceContext;
        this.throwable = throwable;
    }

    public String getService() {
        return service;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
