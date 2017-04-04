/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 18:41 创建
 */
package top.bekit.flow.annotation.listener;

import java.lang.annotation.*;

/**
 * 监听流程异常事件
 * （配合TheFlowListener一起使用；当流程执行过程中发生任何异常，都会调用注入本注解对应的方法；入参类型必须为（Throwable, TargetContext））
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenFlowException {
}
