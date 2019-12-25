/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-22 19:19 创建
 */
package org.bekit.flow.locker;

import org.bekit.common.registrar.AbstractRegistrar;

/**
 * 特定流程加锁器注册器
 */
public class TheFlowLockerRegistrar extends AbstractRegistrar<String, TheFlowLockerExecutor> {
    public TheFlowLockerRegistrar() {
        super(TheFlowLockerExecutor::getFlow);
    }
}
