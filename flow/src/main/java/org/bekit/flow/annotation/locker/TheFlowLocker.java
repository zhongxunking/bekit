/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-15 22:19 创建
 */
package org.bekit.flow.annotation.locker;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 特定流程加锁器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TheFlowLocker {
    /**
     * 加锁的流程
     */
    String flow();
}
