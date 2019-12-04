/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.annotation.service;

import java.lang.annotation.*;

/**
 * 服务执行（如果@Service的enableTx属性为true，则在执行前会开启新事务；如果本注解标注的方法无异常抛出，则会提交事务，否则会回滚事务）
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceExecute {
}
