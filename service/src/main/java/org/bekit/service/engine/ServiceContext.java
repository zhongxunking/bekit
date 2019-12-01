/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.service.engine;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 服务上下文
 */
@Getter
public class ServiceContext<O, R> {
    // 入参
    private final O order;
    // 结果
    private final R result;
    // 附件（可往里面设值，可传递一些附加信息）
    private final Map<Object, Object> attachment;

    public ServiceContext(O order, R result, Map<Object, Object> attachment) {
        Assert.notNull(order, "order不能为null");
        Assert.notNull(result, "result不能为null");
        Assert.notNull(attachment, "attachment不能为null");
        this.order = order;
        this.result = result;
        this.attachment = attachment;
    }
}
