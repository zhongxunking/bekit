/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.bus;

import top.bekit.event.listener.ListenerExecutor;

import java.util.*;

/**
 * 事件总线
 */
public class EventBus {
    // 监听器执行器
    private List<ListenerExecutor> listenerExecutors = new ArrayList<>();
    // 优先级升序监听器执行器缓存
    private Map<Class, List<ListenerExecutor>> priorityAscListenerExecutorsCache = new HashMap<>();
    // 优先级降序监听器执行器缓存
    private Map<Class, List<ListenerExecutor>> priorityDescListenerExecutorsCache = new HashMap<>();

    /**
     * 注册监听器
     *
     * @param listenerExecutor 监听器执行器
     */
    public void register(ListenerExecutor listenerExecutor) {
        listenerExecutors.add(listenerExecutor);
        Collections.sort(listenerExecutors);
        refreshListenerCache();
    }

    /**
     * 分派事件
     * （先执行优先级升序，再执行优先级降序）
     *
     * @param event 事件
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void dispatch(Object event) throws Throwable {
        // 执行优先级升序
        List<ListenerExecutor> cachedListenerExecutors = priorityAscListenerExecutorsCache.get(event.getClass());
        if (cachedListenerExecutors != null) {
            for (ListenerExecutor listenerExecutor : cachedListenerExecutors) {
                listenerExecutor.execute(event);
            }
        }
        // 执行优先级降序
        cachedListenerExecutors = priorityDescListenerExecutorsCache.get(event.getClass());
        if (cachedListenerExecutors != null) {
            for (ListenerExecutor listenerExecutor : cachedListenerExecutors) {
                listenerExecutor.execute(event);
            }
        }
    }

    // 刷新监听器缓存
    private void refreshListenerCache() {
        // 刷新优先级升序缓存
        priorityAscListenerExecutorsCache = new HashMap<>();
        // 获取本总线所有的优先级升序事件类型
        Set<Class> eventTypes = new HashSet<>();
        for (ListenerExecutor listenerExecutor : listenerExecutors) {
            eventTypes.addAll(listenerExecutor.getEventTypes(true));
        }
        // 根据事件类型设置缓存
        for (Class eventType : eventTypes) {
            // 获取指定事件类型的监听器
            List<ListenerExecutor> cachedListenerExecutors = new ArrayList<>();
            for (ListenerExecutor listenerExecutor : listenerExecutors) {
                if (listenerExecutor.getEventTypes(true).contains(eventType)) {
                    cachedListenerExecutors.add(listenerExecutor);
                }
            }
            // 设置缓存
            priorityAscListenerExecutorsCache.put(eventType, cachedListenerExecutors);
        }

        // 刷新优先级降序缓存
        priorityDescListenerExecutorsCache = new HashMap<>();
        // 获取本总线所有的优先级降序事件类型
        eventTypes = new HashSet<>();
        for (ListenerExecutor listenerExecutor : listenerExecutors) {
            eventTypes.addAll(listenerExecutor.getEventTypes(false));
        }
        // 根据事件类型设置缓存
        for (Class eventType : eventTypes) {
            // 获取指定事件类型的监听器
            List<ListenerExecutor> cachedListenerExecutors = new ArrayList<>();
            for (int i = listenerExecutors.size() - 1; i >= 0; i--) {
                if (listenerExecutors.get(i).getEventTypes(false).contains(eventType)) {
                    cachedListenerExecutors.add(listenerExecutors.get(i));
                }
            }
            // 设置缓存
            priorityDescListenerExecutorsCache.put(eventType, cachedListenerExecutors);
        }
    }
}
