/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:33 创建
 */
package top.bekit.flow.annotation.processor;

import java.lang.annotation.*;

/**
 * 前置处理（可选要素）
 * <p>
 * 一般进行预处理，比如一些预校验。
 * 有入参的话，则必须是TargetContext类型，返回值必须是void（比如：void before(TargetContext targetContext)）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
