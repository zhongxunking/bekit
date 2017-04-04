/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 18:23 创建
 */
package top.bekit.flow.listener;

import org.springframework.util.ClassUtils;
import top.bekit.common.method.MethodExecutor;
import top.bekit.flow.annotation.listener.ListenFlowException;
import top.bekit.flow.annotation.listener.ListenNodeDecide;
import top.bekit.flow.annotation.listener.TheFlowListener;
import top.bekit.flow.engine.TargetContext;
import top.bekit.flow.listener.TheFlowListenerExecutor.ListenFlowExceptionMethodExecutor;
import top.bekit.flow.listener.TheFlowListenerExecutor.ListenNodeDecideMethodExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 特定流程监听器解析器
 */
public class TheFlowListenerParser {

    /**
     * 解析特定流程监听器
     *
     * @param theFlowListener 特定流程监听器
     * @return 特定流程监听器执行器
     */
    public static TheFlowListenerExecutor parseTheFlowListener(Object theFlowListener) {
        // 获取被监听的流程名称
        TheFlowListener theFlowListenerAnnotation = theFlowListener.getClass().getAnnotation(TheFlowListener.class);
        // 创建特定流程监听器执行器
        TheFlowListenerExecutor theFlowListenerExecutor = new TheFlowListenerExecutor(theFlowListenerAnnotation.flow(), theFlowListener);
        for (Method method : theFlowListener.getClass().getDeclaredMethods()) {
            for (Class clazz : TheFlowListenerExecutor.LISTEN_METHOD_ANNOTATIONS) {
                if (method.isAnnotationPresent(clazz)) {
                    // 设置监听方法执行器
                    theFlowListenerExecutor.setListenMethodExecutor(clazz, parseListenMethodExecutor(clazz, method));
                    break;
                }
            }
        }
        theFlowListenerExecutor.validate();

        return theFlowListenerExecutor;
    }

    // 解析监听方法
    private static MethodExecutor parseListenMethodExecutor(Class clazz, Method method) {
        // 校验方法类型
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("监听节点选择方法" + ClassUtils.getQualifiedMethodName(method) + "必须是public类型");
        }
        // 校验返回类型
        if (method.getReturnType() != void.class) {
            throw new RuntimeException("特定流程监听方法" + ClassUtils.getQualifiedMethodName(method) + "的返回类型必须是void");
        }
        // 校验入参
        if (clazz == ListenNodeDecide.class) {
            checkListenNodeDecideMethodParameterTypes(method);
            return new ListenNodeDecideMethodExecutor(method);
        } else if (clazz == ListenFlowException.class) {
            checkListenFlowExceptionMethodParameterTypes(method);
            return new ListenFlowExceptionMethodExecutor(method);
        } else {
            throw new IllegalArgumentException("非法的特定流程监听器监听方法类型" + ClassUtils.getShortName(clazz));
        }
    }

    // 校验监听节点选择方法入参类型
    private static void checkListenNodeDecideMethodParameterTypes(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 2) {
            if (parameterTypes[0] != String.class || parameterTypes[1] != TargetContext.class) {
                throw new RuntimeException("监听节点选择方法" + ClassUtils.getQualifiedMethodName(method) + "要么没有入参，要么入参是（String, TargetContext）");
            }
        } else if (parameterTypes.length != 0) {
            throw new RuntimeException("监听节点选择方法" + ClassUtils.getQualifiedMethodName(method) + "要么没有入参，要么入参是（String, TargetContext）");
        }
    }

    // 校验监听流程异常方法入参类型
    private static void checkListenFlowExceptionMethodParameterTypes(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 2) {
            if (parameterTypes[0] != Throwable.class || parameterTypes[1] != TargetContext.class) {
                throw new RuntimeException("监听流程异常方法" + ClassUtils.getQualifiedMethodName(method) + "要么没有入参，要么入参是（Throwable, TargetContext）");
            }
        } else if (parameterTypes.length != 0) {
            throw new RuntimeException("监听流程异常方法" + ClassUtils.getQualifiedMethodName(method) + "要么没有入参，要么入参是（Throwable, TargetContext）");
        }
    }
}
