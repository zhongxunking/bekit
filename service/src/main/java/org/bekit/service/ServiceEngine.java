/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service;

import java.util.Map;

/**
 * 服务引擎
 */
public interface ServiceEngine {
    /**
     * 执行服务
     *
     * @param service 服务名称
     * @param order   入参
     * @param <O>     入参类型
     * @param <R>     出参类型
     * @return 出参
     */
    <O, R> R execute(String service, O order);

    /**
     * 执行服务
     *
     * @param service    服务名称
     * @param order      入参
     * @param attachment 附件
     * @param <O>        入参类型
     * @param <R>        出参类型
     * @return 出参
     */
    <O, R> R execute(String service, O order, Map<Object, Object> attachment);
}
