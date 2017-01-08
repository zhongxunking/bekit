/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-23 21:24 创建
 */
package top.bekit.flow.boot;

/**
 * 配置属性key定义类
 * (目前只定义了流程引擎，以后开发其他框架后也会在此定义)
 */
public final class ConfigurationKey {

    /**
     * 流程配置key定义
     */
    // 流程引擎配置key前缀
    public static final String FLOW_PREFIX = "bekit.flow";
    // 流程引擎配置开启key
    public static final String FLOW_ENABLE = FLOW_PREFIX + ".enable";

}
