/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.event.listener;

import org.bekit.event.annotation.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 监听器持有器（会被注册到spring容器中）
 */
public class ListenerHolder {
    @Autowired
    private ApplicationContext applicationContext;
    // 监听器执行器Map（key：监听器的类型）
    private Map<Class, List<ListenerExecutor>> listenerExecutorsMap = new HashMap<>();

    // 初始化（查询spring容器中所有的@Listener监听器并解析，spring自动执行）
    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(Listener.class);
        for (String beanName : beanNames) {
            // 解析监听器
            ListenerExecutor listenerExecutor = ListenerParser.parseListener(applicationContext.getBean(beanName));
            // 将执行器放入持有器中
            List<ListenerExecutor> listenerExecutors = listenerExecutorsMap.get(listenerExecutor.getType());
            if (listenerExecutors == null) {
                listenerExecutors = new ArrayList<>();
                listenerExecutorsMap.put(listenerExecutor.getType(), listenerExecutors);
            }
            listenerExecutors.add(listenerExecutor);
        }
    }

    /**
     * 获取所有的监听器类型
     */
    public Set<Class> getTypes() {
        return listenerExecutorsMap.keySet();
    }

    /**
     * 获取指定类型的监听器执行器
     *
     * @param type 监听器类型
     * @return 如果不存在该类型的监听器执行器，则返回空List
     */
    public List<ListenerExecutor> getListenerExecutors(Class type) {
        if (!listenerExecutorsMap.containsKey(type)) {
            return new ArrayList<>();
        }
        return listenerExecutorsMap.get(type);
    }
}
