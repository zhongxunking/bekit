/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 12:14 创建
 */
package top.bekit.flow.engine;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 目标上下文
 */
public class TargetContext {
    // 目标实体
    private Object target;
    // 附件（一般存的是target不包含的信息，但在流程执行中又需要用到的信息）
    private Map<Object, Object> attachment;

    public TargetContext(Object target, Map<Object, Object> attachment) {
        Assert.notNull(target, "目标对象不能为null");
        this.target = target;
        this.attachment = attachment;
        if (this.attachment == null) {
            this.attachment = new HashMap<Object, Object>();
        }
    }

    /**
     * 获取目标实体
     */
    public <T> T getTarget() {
        return (T) target;
    }

    /**
     * 刷新目标对象
     *
     * @param target 目标对象（会替换掉目标上下文中原有的目标对象）
     */
    public void refreshTarget(Object target) {
        Assert.notNull(target, "目标对象不能为null");
        this.target = target;
    }

    /**
     * 获取附件属性
     */
    public <T> T getAttachmentAttribute(Object key) {
        return (T) attachment.get(key);
    }

    /**
     * 设置附件属性
     */
    public void setAttachmentAttribute(Object key, Object value) {
        attachment.put(key, value);
    }
}
