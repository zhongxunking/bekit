/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * 事件发布器
 */
public interface EventPublisher {

    /**
     * 发布事件
     *
     * @param event 事件
     * @throws UndeclaredThrowableException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成UndeclaredThrowableException异常，
     *                                      目的是让客户代码不用每次调用时都需要catch
     */
    void publish(Object event);

}
