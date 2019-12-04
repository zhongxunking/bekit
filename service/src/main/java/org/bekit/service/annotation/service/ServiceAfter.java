/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-09-07 14:17 创建
 */
package org.bekit.service.annotation.service;

import java.lang.annotation.*;

/**
 * 服务后置处理（执行中不会有事务）
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceAfter {
}
