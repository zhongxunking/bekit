/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-24 22:23 创建
 */
package org.bekit.common.scanner;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.annotation.Annotation;

/**
 * 抽象扫描器
 */
@AllArgsConstructor
public abstract class AbstractScanner implements ApplicationListener<ContextRefreshedEvent> {
    // 需扫描的注解类型
    private final Class<? extends Annotation> annotationType;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 扫描
        String[] beanNames = event.getApplicationContext().getBeanNamesForAnnotation(annotationType);
        for (String beanName : beanNames) {
            onScan(event.getApplicationContext().getBean(beanName));
        }
    }

    /**
     * 扫描模版方法
     *
     * @param obj 扫描到的对象
     */
    protected abstract void onScan(Object obj);
}
