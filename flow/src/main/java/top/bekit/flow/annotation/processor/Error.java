/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:57 创建
 */
package top.bekit.flow.annotation.processor;

import java.lang.annotation.*;

/**
 * 错误处理（可选要素）
 *
 * 在执行@Before、@Execute、@After任何一个发生异常时会执行。
 * 有入参的话，则必须是TargetContext类型，返回值必须是void（比如：void error(TargetContext targetContext)）
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Error {
}
