/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.annotation.listener;

import java.lang.annotation.*;

/**
 * 监听
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listen {

    /**
     * 是否按照优先级升序
     * <p>
     * true：表示升序，即监听器中优先级值越小优先级越高；false：表示降序，即监听器中优先级值越大优先级越高。默认为升序。
     * 当一个事件发布时，总是先执行完优先级为升序的监听方法，再执行优先级为降序的监听方法
     */
    boolean priorityAsc() default true;

}
