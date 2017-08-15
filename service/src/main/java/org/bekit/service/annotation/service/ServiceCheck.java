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
 * 服务校验（业务校验）
 * <p>
 * 校验过程中发生任何异常（比如空指针异常），给上层返回的应答都应该是业务失败。
 * 因为在校验阶段即没有真正执行业务，又没有保存任何数据
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceCheck {
}
