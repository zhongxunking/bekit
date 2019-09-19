/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.annotation.service;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 服务
 * <p>
 * 执行顺序：@ServiceBefore（如果存在）、@ServiceExecute、@ServiceAfter（如果存在）
 * 如果enableTx为true，则在执行@ServiceExecute前先开启新事务，如果@ServiceExecute标注的方法无异常抛出，则会提交事务，否则会回滚事务
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
    /**
     * 服务名称（默认使用被注解的类名，首字母小写）
     */
    String name() default "";

    /**
     * 是否开启事务（默认关闭）
     */
    boolean enableTx() default false;
}
