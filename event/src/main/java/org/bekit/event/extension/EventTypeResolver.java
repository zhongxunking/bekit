/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 11:33 创建
 */
package org.bekit.event.extension;

/**
 * 事件类型解决器
 */
public interface EventTypeResolver {

    /**
     * 根据事件得到对应的事件类型
     *
     * @param event 事件
     * @return 事件类型
     */
    Object resolve(Object event);
}
