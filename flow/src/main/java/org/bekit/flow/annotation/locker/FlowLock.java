/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-15 22:25 创建
 */
package org.bekit.flow.annotation.locker;

import java.lang.annotation.*;

/**
 * 流程加锁
 * <p>
 * 被标记的方法入参为FlowContext，返回值为加锁后的目标对象。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlowLock {
}
