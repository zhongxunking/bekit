/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.listener;

import org.springframework.util.ClassUtils;
import top.bekit.common.method.MethodExecutor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 监听器执行器
 */
public class ListenerExecutor implements Comparable<ListenerExecutor> {
    // 监听器
    private Object listener;
    // 监听器类型
    private Class type;
    // 优先级
    private int priority;
    // 监听执行器map（key：被监听的事件类型）
    private Map<Class, ListenExecutor> listenExecutorMap = new HashMap<>();

    public ListenerExecutor(Object listener, Class type, int priority) {
        this.listener = listener;
        this.type = type;
        this.priority = priority;
    }

    /**
     * 执行监听事件
     *
     * @param event 事件
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void execute(Object event) throws Throwable {
        ListenExecutor listenExecutor = listenExecutorMap.get(event.getClass());
        if (listenExecutor != null) {
            listenExecutor.execute(listener, event);
        }
    }

    /**
     * 添加监听执行器
     *
     * @param listenExecutor 监听执行器
     * @throws IllegalStateException 如果已存在相同事件类型的监听执行器
     */
    public void addListenExecutor(ListenExecutor listenExecutor) {
        if (listenExecutorMap.containsKey(listenExecutor.getEventType())) {
            throw new IllegalStateException("监听器" + ClassUtils.getShortName(listener.getClass()) + "存在多个监听" + ClassUtils.getShortName(listenExecutor.getEventType()) + "事件的方法");
        }
        listenExecutorMap.put(listenExecutor.getEventType(), listenExecutor);
    }

    /**
     * 获取监听器类型
     */
    public Class getType() {
        return type;
    }

    /**
     * 获取优先级
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 获取指定优先级顺序的监听事件类型
     *
     * @param priorityAsc 是否优先级升序（true：升序，false：降序）
     */
    public Set<Class> getEventTypes(boolean priorityAsc) {
        Set<Class> eventTypes = new HashSet<>();
        for (Class eventType : listenExecutorMap.keySet()) {
            ListenExecutor listenExecutor = listenExecutorMap.get(eventType);
            if (listenExecutor.isPriorityAsc() == priorityAsc) {
                eventTypes.add(eventType);
            }
        }
        return eventTypes;
    }

    @Override
    public int compareTo(ListenerExecutor listenerExecutor) {
        return priority - listenerExecutor.getPriority();
    }

    /**
     * 校验监听器执行器是否有效
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (listener == null || type == null) {
            throw new IllegalStateException("监听器内部要素不全");
        }
    }

    /**
     * 监听执行器
     */
    public static class ListenExecutor extends MethodExecutor {
        // 是否优先级升序
        private boolean priorityAsc;
        // 事件类型
        private Class eventType;

        public ListenExecutor(boolean priorityAsc, Method targetMethod) {
            super(targetMethod);
            this.priorityAsc = priorityAsc;
            this.eventType = getParameterTypes()[0];
        }

        /**
         * 执行监听
         *
         * @param listener 监听器
         * @param event    事件
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object listener, Object event) throws Throwable {
            execute(listener, new Object[]{event});
        }

        /**
         * 获取事件类型
         */
        public Class getEventType() {
            return eventType;
        }

        /**
         * 是否是优先级升序
         */
        public boolean isPriorityAsc() {
            return priorityAsc;
        }
    }
}
