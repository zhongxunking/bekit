/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-28 23:21 创建
 */
package top.bekit.flow.annotation.transaction;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 流程事务
 * （一个流程最多能有一个流程事务类）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface FlowTx {

    /**
     * 对应的流程
     */
    String flow();

}
