/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:33 创建
 */
package org.bekit.flow.annotation.processor;

import java.lang.annotation.*;

/**
 * 前置处理（可选）
 * <p>
 * 一般进行预处理，比如一些预校验。
 * 入参必须是TargetContext类型，返回值必须是void（比如：void before(TargetContext targetContext)）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorBefore {
}
