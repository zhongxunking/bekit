/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.listener;

import org.bekit.event.annotation.listener.Listener;
import org.bekit.event.extension.ListenerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 监听器持有器
 */
@Component
public class ListenersHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 监听器执行器Map（key：监听器的类型）
    private final Map<Class<? extends ListenerType>, Set<ListenerExecutor>> listenerExecutorsMap = new HashMap<>();

    // 初始化
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Listener.class);
        for (String beanName : beanNames) {
            // 解析监听器
            ListenerExecutor listenerExecutor = ListenerParser.parseListener(applicationContext.getBean(beanName));
            // 将执行器放入持有器中
            Set<ListenerExecutor> listenerExecutors = listenerExecutorsMap.computeIfAbsent(listenerExecutor.getType(), type -> new HashSet<>());
            listenerExecutors.add(listenerExecutor);
        }
    }

    /**
     * 获取所有的监听器类型
     */
    public Set<Class<? extends ListenerType>> getListenerTypes() {
        return Collections.unmodifiableSet(listenerExecutorsMap.keySet());
    }

    /**
     * 获取指定类型的监听器执行器
     *
     * @param listenerType 监听器类型
     */
    public Set<ListenerExecutor> getListenerExecutors(Class<? extends ListenerType> listenerType) {
        Set<ListenerExecutor> listenerExecutors = listenerExecutorsMap.get(listenerType);
        if (listenerExecutors == null) {
            listenerExecutors = new HashSet<>();
        }
        return listenerExecutors;
    }
}
