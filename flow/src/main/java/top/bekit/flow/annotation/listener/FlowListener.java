/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 16:27 创建
 */
package top.bekit.flow.annotation.listener;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 流程监听器
 * （一个流程可以有多个监听器）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface FlowListener {

    /**
     * 被监听的流程
     */
    String flow();

}
