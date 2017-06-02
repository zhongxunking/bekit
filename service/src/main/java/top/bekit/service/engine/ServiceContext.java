/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务上下文
 */
public class ServiceContext<O, R> {
    // 入参
    private O order;
    // 结果
    private R result;
    // 附件（可往里面设值，可用于@ServiceCheck和@ServiceExecute之间通信）
    private Map<Object, Object> attachment = new HashMap<>();

    public ServiceContext(O order, R result) {
        this.order = order;
        this.result = result;
    }

    /**
     * 获取order
     */
    public O getOrder() {
        return order;
    }

    /**
     * 获取result
     */
    public R getResult() {
        return result;
    }

    /**
     * 获取附件属性
     */
    public <V> V getAttachmentAttr(Object key) {
        return (V) attachment.get(key);
    }

    /**
     * 设置附件属性
     */
    public void setAttachmentAttr(Object key, Object value) {
        attachment.put(key, value);
    }
}
