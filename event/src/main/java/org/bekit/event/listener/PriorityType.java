/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-04 23:39 创建
 */
package org.bekit.event.listener;

/**
 * 优先级类型（当一个事件发布时，总是先执行完优先级为升序的监听方法，再执行优先级为降序的监听方法）
 */
public enum PriorityType {
    /**
     * 升序（监听器中优先级值越小优先级越高）
     */
    ASC,

    /**
     * 降序（监听器中优先级值越大优先级越高）
     */
    DESC
}
