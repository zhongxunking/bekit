/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 17:46 创建
 */
package org.bekit.flow.annotation.processor;

import java.lang.annotation.*;

/**
 * 处理器执行
 * <p>
 * 被标记的方法入参为FlowContext，返回值作为整个处理器的返回值并返回给流程节点。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorExecute {
}
