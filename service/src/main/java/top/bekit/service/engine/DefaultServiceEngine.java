/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.engine;

import org.springframework.beans.factory.annotation.Autowired;
import top.bekit.event.EventPublisher;
import top.bekit.event.bus.EventBusHolder;
import top.bekit.event.publisher.DefaultEventPublisher;
import top.bekit.service.ServiceEngine;
import top.bekit.service.annotation.listener.ServiceListener;
import top.bekit.service.event.ServiceApplyEvent;
import top.bekit.service.event.ServiceFinishEvent;
import top.bekit.service.service.ServiceExecutor;
import top.bekit.service.service.ServiceHolder;

import javax.annotation.PostConstruct;

/**
 * 服务引擎默认实现类
 */
public class DefaultServiceEngine implements ServiceEngine {
    @Autowired
    private ServiceHolder serviceHolder;
    @Autowired
    private EventBusHolder eventBusHolder;
    // 服务事件发布器
    private EventPublisher eventPublisher;

    // 初始化（创建服务事件发布器，spring自动执行）
    @PostConstruct
    public void init() {
        eventPublisher = new DefaultEventPublisher(eventBusHolder.getEventBus(ServiceListener.class));
    }

    @Override
    public <O, R> R execute(String service, O order, R result) {
        // 构建服务上下文
        ServiceContext serviceContext = new ServiceContext(order, result);
        try {
            // 获取服务执行器
            ServiceExecutor serviceExecutor = serviceHolder.getRequiredServiceExecutor(service);
            // 发布服务申请事件
            eventPublisher.publish(new ServiceApplyEvent(service, serviceContext));
            // 执行服务
            serviceExecutor.execute(serviceContext);
        } finally {
            // 发布服务结束事件
            eventPublisher.publish(new ServiceFinishEvent(service, serviceContext));
        }
        return (R) serviceContext.getResult();
    }
}
