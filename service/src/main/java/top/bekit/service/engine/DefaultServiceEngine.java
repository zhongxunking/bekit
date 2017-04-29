/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.engine;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import top.bekit.event.EventPublisher;
import top.bekit.service.ServiceEngine;
import top.bekit.service.event.ServiceApplyEvent;
import top.bekit.service.event.ServiceExceptionEvent;
import top.bekit.service.event.ServiceFinishEvent;
import top.bekit.service.service.ServiceExecutor;
import top.bekit.service.service.ServiceHolder;

/**
 * 服务引擎默认实现类
 */
public class DefaultServiceEngine implements ServiceEngine {
    @Autowired
    private ServiceHolder serviceHolder;
    // 服务事件发布器
    private EventPublisher eventPublisher;

    public DefaultServiceEngine(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <O, R> R execute(String service, O order) {
        // 获取服务执行器
        ServiceExecutor serviceExecutor = serviceHolder.getRequiredServiceExecutor(service);
        // 校验order类型
        checkOrderClass(order, serviceExecutor);
        // 构建服务上下文
        ServiceContext<O, R> serviceContext = new ServiceContext(order, newResult(serviceExecutor));
        try {
            // 发布服务申请事件
            eventPublisher.publish(new ServiceApplyEvent(service, serviceContext));
            // 执行服务
            serviceExecutor.execute(serviceContext);
        } catch (Throwable e) {
            // 发布服务执行异常事件
            eventPublisher.publish(new ServiceExceptionEvent(service, serviceContext, e));
        } finally {
            // 发布服务结束事件
            eventPublisher.publish(new ServiceFinishEvent(service, serviceContext));
        }
        return serviceContext.getResult();
    }

    // 校验入参order类型
    private void checkOrderClass(Object order, ServiceExecutor serviceExecutor) {
        if (!serviceExecutor.getOrderClass().isAssignableFrom(order.getClass())) {
            throw new IllegalArgumentException("入参order的类型和服务" + serviceExecutor.getServiceName() + "期望的类型不匹配");
        }
    }

    // 创建result
    private Object newResult(ServiceExecutor serviceExecutor) {
        Object result = null;
        try {
            result = serviceExecutor.getResultClass().newInstance();
        } catch (Throwable e) {
            ExceptionUtils.wrapAndThrow(e);
        }
        return result;
    }
}
