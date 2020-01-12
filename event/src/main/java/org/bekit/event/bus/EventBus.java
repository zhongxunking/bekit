/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.bus;

import lombok.RequiredArgsConstructor;
import org.bekit.event.extension.EventTypeResolver;
import org.bekit.event.listener.ListenerExecutor;
import org.bekit.event.listener.PriorityType;

import java.util.*;

/**
 * 事件总线
 */
@RequiredArgsConstructor
public class EventBus {
    // 事件类型解决器
    private final EventTypeResolver eventTypeResolver;
    // 所有监听器执行器
    private final Set<ListenerExecutor> listenerExecutors = new HashSet<>();
    // 分发器
    private Dispatcher dispatcher = new Dispatcher();

    /**
     * 分发事件
     *
     * @param event 事件
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void dispatch(Object event) throws Throwable {
        dispatcher.dispatch(event);
    }

    /**
     * 添加监听器执行器
     *
     * @param listenerExecutor 监听器执行器
     */
    public synchronized void addListenerExecutor(ListenerExecutor listenerExecutor) {
        if (!listenerExecutors.contains(listenerExecutor)) {
            listenerExecutors.add(listenerExecutor);
            dispatcher = dispatcher.addExecutor(listenerExecutor);
        }
    }

    /**
     * 移除监听器执行器
     *
     * @param listenerExecutor 监听器
     */
    public synchronized void removeListenerExecutor(ListenerExecutor listenerExecutor) {
        if (listenerExecutors.contains(listenerExecutor)) {
            listenerExecutors.remove(listenerExecutor);
            dispatcher = dispatcher.removeExecutor(listenerExecutor);
        }
    }

    // 分发器
    private class Dispatcher {
        // 升序队列
        private final Map<Object, List<ListenerExecutor>> asc = new HashMap<>();
        // 降序队列
        private final Map<Object, List<ListenerExecutor>> desc = new HashMap<>();

        // 分发事件
        void dispatch(Object event) throws Throwable {
            Object eventType = eventTypeResolver.resolve(event);
            // 向升序队列分发事件
            doDispatch(asc.get(eventType), event);
            // 向降序队列分发事件
            doDispatch(desc.get(eventType), event);
        }

        // 执行分发事件
        private void doDispatch(List<ListenerExecutor> executors, Object event) throws Throwable {
            if (executors != null) {
                for (ListenerExecutor executor : executors) {
                    executor.execute(event);
                }
            }
        }

        // 新增监听器执行器（返回新的分发器）
        Dispatcher addExecutor(ListenerExecutor executor) {
            Dispatcher nextDispatcher = copy();
            // 调整升序队列
            doAddExecutor(nextDispatcher.asc, executor, PriorityType.ASC, Comparator.comparingInt(ListenerExecutor::getPriority));
            // 调整降序队列
            doAddExecutor(nextDispatcher.desc, executor, PriorityType.DESC, (left, right) -> right.getPriority() - left.getPriority());

            return nextDispatcher;
        }

        // 执行新增监听器执行器
        private void doAddExecutor(Map<Object, List<ListenerExecutor>> executorsMap, ListenerExecutor executor, PriorityType priorityType, Comparator<ListenerExecutor> comparator) {
            for (Object eventType : executor.getEventTypes(priorityType)) {
                List<ListenerExecutor> executors = executorsMap.computeIfAbsent(eventType, k -> new ArrayList<>());
                executors.add(executor);
                executors.sort(comparator);
            }
        }

        // 删除监听器执行器（返回新的分发器）
        Dispatcher removeExecutor(ListenerExecutor executor) {
            Dispatcher nextDispatcher = copy();
            // 调整升序队列
            doRemoveExecutor(nextDispatcher.asc, executor, PriorityType.ASC);
            // 调整降序队列
            doRemoveExecutor(nextDispatcher.desc, executor, PriorityType.DESC);

            return nextDispatcher;
        }

        // 执行删除监听器执行器
        private void doRemoveExecutor(Map<Object, List<ListenerExecutor>> executorsMap, ListenerExecutor executor, PriorityType priorityType) {
            for (Object eventType : executor.getEventTypes(priorityType)) {
                executorsMap.computeIfPresent(eventType, (k, executors) -> {
                    executors.remove(executor);
                    if (executors.isEmpty()) {
                        executors = null;
                    }
                    return executors;
                });
            }
        }

        // 深度复制
        private Dispatcher copy() {
            Dispatcher newDispatcher = new Dispatcher();
            doCopy(asc, newDispatcher.asc);
            doCopy(desc, newDispatcher.desc);
            return newDispatcher;
        }

        // 执行深度复制
        private void doCopy(Map<Object, List<ListenerExecutor>> source, Map<Object, List<ListenerExecutor>> target) {
            source.forEach((eventType, executors) -> {
                List<ListenerExecutor> newExecutors = new ArrayList<>(executors.size() + 1);
                newExecutors.addAll(executors);
                target.put(eventType, newExecutors);
            });
        }
    }
}
