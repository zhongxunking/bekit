/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event;

/**
 * 事件发布器
 */
public interface EventPublisher {

    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publish(Object event);

}
