/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-29 11:42 创建
 */
package org.bekit.event.extension;

/**
 * 监听器类型
 */
public interface ListenerType {

    /**
     * 获取事件类型解决器
     */
    EventTypeResolver getResolver();
}
