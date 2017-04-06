/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import top.bekit.event.annotation.listener.Listen;
import top.bekit.event.annotation.listener.Listener;
import top.bekit.event.listener.ListenerExecutor.ListenExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 监听器解析器
 */
public class ListenerParser {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(ListenerParser.class);

    /**
     * 解析监听器
     *
     * @param listener 监听器
     * @return 监听器执行器
     */
    public static ListenerExecutor parseListener(Object listener) {
        logger.info("解析监听器：{}", listener);
        // 此处得到的@Listener是已经经过@AliasFor属性别名进行属性同步后的结果
        Listener listenerAnnotation = AnnotatedElementUtils.findMergedAnnotation(listener.getClass(), Listener.class);
        // 创建监听器执行器
        ListenerExecutor listenerExecutor = new ListenerExecutor(listener, listenerAnnotation.type(), listenerAnnotation.priority());
        for (Method method : listener.getClass().getDeclaredMethods()) {
            Listen listenAnnotation = method.getAnnotation(Listen.class);
            if (listenAnnotation != null) {
                ListenExecutor listenExecutor = parseListen(listenAnnotation, method);
                listenerExecutor.addListenExecutor(listenExecutor);
            }
        }
        listenerExecutor.validate();

        return listenerExecutor;
    }

    // 解析监听方法
    private static ListenExecutor parseListen(Listen listenAnnotation, Method method) {
        logger.debug("解析监听方法：{}", method);
        // 校验方法类型
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("监听方法" + ClassUtils.getQualifiedMethodName(method) + "必须是public类型");
        }
        // 校验入参
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("监听方法" + ClassUtils.getQualifiedMethodName(method) + "必须只有一个入参");
        }
        // 校验返回类型
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("监听方法" + ClassUtils.getQualifiedMethodName(method) + "的返回必须是void");
        }

        return new ListenExecutor(listenAnnotation.priorityAsc(), method);
    }
}
