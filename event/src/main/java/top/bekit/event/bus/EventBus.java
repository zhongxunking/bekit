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
    // 监听器执行器缓存
    private Map<Class, List<ListenerExecutor>> listenerExecutorsCache = new HashMap<>();

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
        // 获取该事件类型的监听器缓存
        List<ListenerExecutor> theListenerExecutors = listenerExecutorsCache.get(event.getClass());
        if (theListenerExecutors != null) {
            // 执行监听器
            for (ListenerExecutor listenerExecutor : theListenerExecutors) {
                listenerExecutor.execute(event);
            }
        }
    }

    // 刷新监听器缓存
    private void refreshListenerCache() {
        listenerExecutorsCache = new HashMap<>();
        // 获取本总线所有的事件类型
        Set<Class> eventTypes = new HashSet<>();
        for (ListenerExecutor listenerExecutor : listenerExecutors) {
            eventTypes.addAll(listenerExecutor.getEventTypes(true));
            eventTypes.addAll(listenerExecutor.getEventTypes(false));
        }
        // 根据事件类型设置缓存
        for (Class eventType : eventTypes) {
            // 特定事件类型的监听器缓存
            List<ListenerExecutor> theListenerExecutors = new ArrayList<>();
            // 获取指定事件类型的升序监听器
            for (ListenerExecutor listenerExecutor : listenerExecutors) {
                if (listenerExecutor.getEventTypes(true).contains(eventType)) {
                    theListenerExecutors.add(listenerExecutor);
                }
            }
            // 获取指定事件类型的降序监听器
            for (int i = listenerExecutors.size() - 1; i >= 0; i--) {
                ListenerExecutor listenerExecutor = listenerExecutors.get(i);
                if (listenerExecutor.getEventTypes(false).contains(eventType)) {
                    theListenerExecutors.add(listenerExecutor);
                }
            }
            // 设置缓存
            listenerExecutorsCache.put(eventType, theListenerExecutors);
        }
    }
}
