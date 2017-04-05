/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 16:19 创建
 */
package top.bekit.flow.annotation.listener;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 特定流程监听器
 * （监听的是某一个特定流程发生的事件，一个流程最多只能有一个特定流程监听器，配合@ListenNodeDecide、@ListenFlowException一起使用）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface TheFlowListener {

    /**
     * 被监听的流程
     */
    String flow();

}
