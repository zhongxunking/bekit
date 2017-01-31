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
 * 服务完成事件
 */
public class ServiceFinishEvent {
    // 被申请的服务名称
    private String service;
    // 服务上下文
    private ServiceContext serviceContext;

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
