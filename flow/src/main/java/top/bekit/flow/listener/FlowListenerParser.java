/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 23:38 创建
 */
package top.bekit.flow.listener;

import org.springframework.util.ClassUtils;
import top.bekit.flow.annotation.listener.FlowListener;
import top.bekit.flow.annotation.listener.ListenDecideNode;
import top.bekit.flow.engine.TargetContext;
import top.bekit.flow.listener.FlowListenerExecutor.ListenMethodExecutor;

import java.lang.reflect.Method;

/**
 * 流程监听器解析器
 */
public class FlowListenerParser {

    /**
     * 解析流程监听器
     *
     * @param flowListener 流程监听器
     * @return 流程监听器执行器
     */
    public static FlowListenerExecutor parseFlowListener(Object flowListener) {
        FlowListener flowListenerAnnotation = flowListener.getClass().getAnnotation(FlowListener.class);
        // 创建流程监听器执行器
        FlowListenerExecutor flowListenerExecutor = new FlowListenerExecutor(flowListenerAnnotation.flow(), flowListener);
        for (Method method : flowListener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ListenDecideNode.class)) {
                // 添加监听节点选择执行器
                flowListenerExecutor.addListenDecideNodeExecutor(parseListenMethod(method.getAnnotation(ListenDecideNode.class).nodeExpression(), method));
            }
        }
        flowListenerExecutor.validate();

        return flowListenerExecutor;
    }

    // 解析监听方法
    private static ListenMethodExecutor parseListenMethod(String expression, Method listenMethod) {
        boolean hasParameter;
        // 判断是否有入参+校验入参
        Class[] parameterTypes = listenMethod.getParameterTypes();
        if (parameterTypes.length == 0) {
            hasParameter = false;
        } else if (parameterTypes.length == 2) {
            if (parameterTypes[0] != String.class || parameterTypes[1] != TargetContext.class) {
                throw new RuntimeException("流程监听方法" + ClassUtils.getQualifiedMethodName(listenMethod) + "的入参必须是（String，TargetContext）");
            }
            hasParameter = true;
        } else {
            throw new RuntimeException("流程监听方法" + ClassUtils.getQualifiedMethodName(listenMethod) + "要么没有入参，要么入参是（String, TargetContext）");
        }
        // 校验返回类型
        if (listenMethod.getReturnType() != void.class) {
            throw new RuntimeException("流程监听方法" + ClassUtils.getQualifiedMethodName(listenMethod) + "的返回类型必须是void");
        }

        return new ListenMethodExecutor(expression, listenMethod, hasParameter);
    }
}
