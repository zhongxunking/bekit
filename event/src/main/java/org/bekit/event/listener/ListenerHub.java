/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.listener;

import org.bekit.event.extension.ListenerType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听器中心
 */
@Component
public class ListenerHub {
    // 监听器执行器Map（key：监听器的类型）
    private final Map<Class<? extends ListenerType>, Set<ListenerExecutor>> listenerExecutorMap = new ConcurrentHashMap<>();

    /**
     * 新增监听器
     *
     * @param listenerExecutor 监听器执行器
     */
    public void addListener(ListenerExecutor listenerExecutor) {
        listenerExecutorMap.compute(listenerExecutor.getType(), (type, listenerExecutors) -> {
            if (listenerExecutors == null) {
                listenerExecutors = new HashSet<>();
            }
            listenerExecutors.add(listenerExecutor);
            return listenerExecutors;
        });
    }

    /**
     * 获取所有的监听器类型
     *
     * @return 所有的监听器类型
     */
    public Set<Class<? extends ListenerType>> getTypes() {
        return Collections.unmodifiableSet(listenerExecutorMap.keySet());
    }

    /**
     * 获取指定类型的所有监听器
     *
     * @param type 类型
     * @return 指定类型的所有监听器
     */
    public Set<ListenerExecutor> getListeners(Class<? extends ListenerType> type) {
        Set<ListenerExecutor> listenerExecutors = listenerExecutorMap.get(type);
        if (listenerExecutors == null) {
            listenerExecutors = new HashSet<>();
        }
        return Collections.unmodifiableSet(listenerExecutors);
    }
}
