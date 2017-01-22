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
    private List<ListenerExecutor> listenerExecutors = new ArrayList<ListenerExecutor>();
    // 监听器执行器缓存
    private Map<Class, List<ListenerExecutor>> listenerExecutorsCache;

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
     *
     * @param event 事件
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void dispatch(Object event) throws Throwable {
        List<ListenerExecutor> cachedListenerExecutors = listenerExecutorsCache.get(event.getClass());
        if (cachedListenerExecutors != null) {
            for (ListenerExecutor listenerExecutor : cachedListenerExecutors) {
                listenerExecutor.execute(event);
            }
        }
    }

    // 刷新监听器缓存
    private void refreshListenerCache() {
        listenerExecutorsCache = new HashMap<Class, List<ListenerExecutor>>();
        // 获取本总线所有的事件类型
        Set<Class> eventTypes = new HashSet<Class>();
        for (ListenerExecutor listenerExecutor : listenerExecutors) {
            eventTypes.addAll(listenerExecutor.getEventTypes());
        }
        // 根据事件类型设置缓存
        for (Class eventType : eventTypes) {
            // 获取指定事件类型的监听器
            List<ListenerExecutor> cachedListenerExecutors = new ArrayList<ListenerExecutor>();
            for (ListenerExecutor listenerExecutor : listenerExecutors) {
                if (listenerExecutor.getEventTypes().contains(eventType)) {
                    cachedListenerExecutors.add(listenerExecutor);
                }
            }
            // 设置缓存
            listenerExecutorsCache.put(eventType, cachedListenerExecutors);
        }
    }
}
