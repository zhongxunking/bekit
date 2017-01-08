/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-28 23:24 创建
 */
package top.bekit.flow.annotation.transaction;

import java.lang.annotation.*;

/**
 * 锁目标对象
 * <p>
 * 流程引擎每次开启新事务时都会调用@LockTarget类型方法，用于锁住目标对象
 * 注意：锁住目标对象后，应该更新目标对象到目标上下文
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockTarget {
}
