/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.event;

import top.bekit.service.engine.ServiceContext;

/**
 * 服务异常事件
 */
public class ServiceExceptionEvent {
    // 服务名称
    private String serviceName;
    // 服务上下文
    private ServiceContext serviceContext;
    // 目标异常
    private Throwable targetException;

    public ServiceExceptionEvent(String serviceName, ServiceContext serviceContext, Throwable targetException) {
        this.serviceName = serviceName;
        this.serviceContext = serviceContext;
        this.targetException = targetException;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    public Throwable getTargetException() {
        return targetException;
    }
}
