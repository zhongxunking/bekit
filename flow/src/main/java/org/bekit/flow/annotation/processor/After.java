/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:53 创建
 */
package org.bekit.flow.annotation.processor;

import java.lang.annotation.*;

/**
 * 后置处理（可选要素）
 * <p>
 * 一般是业务处理后的收尾工作。
 * 有入参的话，则必须是TargetContext类型，返回值必须是void（比如：void after(TargetContext targetContext)）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
}
