/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 12:14 创建
 */
package org.bekit.flow.engine;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 目标上下文
 */
@Getter
public class TargetContext<T> {
    // 目标对象
    private T target;
    // 附件（可往里面设值，可传递一些附加信息）
    private final Map<Object, Object> attachment;

    public TargetContext(T target, Map<Object, Object> attachment) {
        Assert.notNull(target, "target不能为null");
        Assert.notNull(attachment, "attachment不能为null");
        this.target = target;
        this.attachment = attachment;
    }

    /**
     * 刷新目标对象
     *
     * @param newTarget 新的目标对象（会替换掉目标上下文中原有的目标对象）
     */
    public void refreshTarget(T newTarget) {
        Assert.notNull(newTarget, "目标对象不能为null");
        this.target = newTarget;
    }
}
