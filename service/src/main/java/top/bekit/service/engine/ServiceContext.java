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
public class ServiceContext {
    // 入参
    private Object order;
    // 结果
    private Object result;

    public ServiceContext(Object order, Object result) {
        this.order = order;
        this.result = result;
    }

    public Object getOrder() {
        return order;
    }

    public Object getResult() {
        return result;
    }
}
