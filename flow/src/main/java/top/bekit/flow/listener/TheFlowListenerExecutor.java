/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-04-04 16:23 创建
 */
package top.bekit.flow.listener;

import org.springframework.util.ClassUtils;
import top.bekit.common.method.MethodExecutor;
import top.bekit.flow.annotation.listener.ListenFlowException;
import top.bekit.flow.annotation.listener.ListenNodeDecide;
import top.bekit.flow.engine.TargetContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 特定流程监听器执行器
 */
public class TheFlowListenerExecutor {
    /**
     * 特定流程监听器方法注解
     */
    public static final Class[] LISTEN_METHOD_ANNOTATIONS = {ListenNodeDecide.class, ListenFlowException.class};
    // 监听方法对应的执行器（key：监听方法注解的CLass）
    private static final Map<Class, Class> LISTEN_METHOD_EXECUTOR_MAP;

    // 初始化LISTEN_METHOD_EXECUTOR_MAP
    static {
        LISTEN_METHOD_EXECUTOR_MAP = new HashMap<>();
        LISTEN_METHOD_EXECUTOR_MAP.put(ListenNodeDecide.class, ListenNodeDecideMethodExecutor.class);
        LISTEN_METHOD_EXECUTOR_MAP.put(ListenFlowException.class, ListenFlowExceptionMethodExecutor.class);
    }

    // 被监听的流程名称
    private String flow;
    // 特定流程监听器
    private Object theFlowListener;
    // 方法执行器Map（key：监听方法注解的CLass）
    private Map<Class, MethodExecutor> methodExecutorMap = new HashMap<>();

    public TheFlowListenerExecutor(String flow, Object theFlowListener) {
        this.flow = flow;
        this.theFlowListener = theFlowListener;
    }

    /**
     * 监听节点选择事件
     *
     * @param node          被选择的节点
     * @param targetContext 目标上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void listenNodeDecide(String node, TargetContext targetContext) throws Throwable {
        ListenNodeDecideMethodExecutor methodExecutor = (ListenNodeDecideMethodExecutor) methodExecutorMap.get(ListenNodeDecide.class);
        if (methodExecutor != null) {
            methodExecutor.execute(theFlowListener, node, targetContext);
        }
    }

    /**
     * 监听流程异常事件
     *
     * @param throwable     发生的异常
     * @param targetContext 目标上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void listenFlowException(Throwable throwable, TargetContext targetContext) throws Throwable {
        ListenFlowExceptionMethodExecutor methodExecutor = (ListenFlowExceptionMethodExecutor) methodExecutorMap.get(ListenFlowException.class);
        if (methodExecutor != null) {
            methodExecutor.execute(theFlowListener, throwable, targetContext);
        }
    }

    /**
     * 设置监听方法执行器
     *
     * @param clazz          类型
     * @param methodExecutor 方法执行器
     */
    public void setListenMethodExecutor(Class clazz, MethodExecutor methodExecutor) {
        if (!LISTEN_METHOD_EXECUTOR_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("流程监听方法类型" + ClassUtils.getShortName(clazz) + "不合法");
        }
        if (methodExecutor.getClass() != LISTEN_METHOD_EXECUTOR_MAP.get(clazz)) {
            throw new IllegalArgumentException("流程监听方法处理器" + ClassUtils.getShortName(methodExecutor.getClass()) + "不合法");
        }
        if (methodExecutorMap.containsKey(clazz)) {
            throw new IllegalStateException("特定流程监听器" + ClassUtils.getShortName(theFlowListener.getClass()) + "存在多个@" + ClassUtils.getShortName(clazz) + "类型的流程监听方法");
        }
        methodExecutorMap.put(clazz, methodExecutor);
    }

    /**
     * 获取被监听的流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 校验特定流程监听器执行器是否有效
     *
     * @throws IllegalStateException 校验不通过
     */
    public void validate() {
        if (flow == null || theFlowListener == null) {
            throw new IllegalStateException("特定流程监听器内部要素不全");
        }
    }

    /**
     * 监听节点选择事件方法执行器
     */
    public static class ListenNodeDecideMethodExecutor extends MethodExecutor {

        public ListenNodeDecideMethodExecutor(Method targetMethod) {
            super(targetMethod);
        }

        /**
         * 执行
         *
         * @param theFlowListener 特定流程监听器
         * @param node            被选择的节点
         * @param targetContext   目标上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object theFlowListener, String node, TargetContext targetContext) throws Throwable {
            execute(theFlowListener, new Object[]{node, targetContext});
        }
    }

    /**
     * 监听流程异常事件方法执行器
     */
    public static class ListenFlowExceptionMethodExecutor extends MethodExecutor {

        public ListenFlowExceptionMethodExecutor(Method targetMethod) {
            super(targetMethod);
        }

        /**
         * 执行
         *
         * @param theFlowListener 特定流程监听器
         * @param throwable       发生的异常
         * @param targetContext   目标上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object theFlowListener, Throwable throwable, TargetContext targetContext) throws Throwable {
            execute(theFlowListener, new Object[]{throwable, targetContext});
        }
    }

}
