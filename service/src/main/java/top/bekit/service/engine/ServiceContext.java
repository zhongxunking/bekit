/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.engine;

/**
 * 服务上下文
 */
public class ServiceContext<O, R> {
    // 入参
    private O order;
    // 结果
    private R result;

    public ServiceContext(O order, R result) {
        this.order = order;
        this.result = result;
    }

    public O getOrder() {
        return order;
    }

    public R getResult() {
        return result;
    }
}
