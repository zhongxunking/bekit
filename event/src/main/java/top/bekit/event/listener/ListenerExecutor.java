/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.listener;

import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
    private Map<Class, ListenExecutor> listenExecutorMap = new HashMap<Class, ListenExecutor>();

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
        if (listenExecutorMap.containsKey(event.getClass())) {
            ListenExecutor listenExecutor = listenExecutorMap.get(event.getClass());
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
     * 获取监听的所有事件类型
     */
    public Set<Class> getEventTypes() {
        return listenExecutorMap.keySet();
    }

    @Override
    public int compareTo(ListenerExecutor listenerExecutor) {
        return priority - listenerExecutor.getPriority();
    }

    /**
     * 监听执行器
     */
    public static class ListenExecutor {
        // 目标方法
        private Method targetMethod;
        // 事件类型
        private Class eventType;

        public ListenExecutor(Method targetMethod, Class eventType) {
            this.targetMethod = targetMethod;
            this.eventType = eventType;
        }

        /**
         * 执行监听
         *
         * @param listener 监听器
         * @param event    事件
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object listener, Object event) throws Throwable {
            try {
                targetMethod.invoke(listener, event);
            } catch (InvocationTargetException e) {
                // 抛出原始异常
                throw e.getTargetException();
            }
        }

        /**
         * 获取事件类型
         */
        public Class getEventType() {
            return eventType;
        }
    }
}
