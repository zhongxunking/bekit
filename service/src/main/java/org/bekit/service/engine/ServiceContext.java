/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 服务上下文
 */
@AllArgsConstructor
@Getter
public class ServiceContext<O, R> {
    // 入参
    private final O order;
    // 结果
    private final R result;
    // 附件（可往里面设值，可传递一些附加信息）
    private final Map<Object, Object> attachment;
}
