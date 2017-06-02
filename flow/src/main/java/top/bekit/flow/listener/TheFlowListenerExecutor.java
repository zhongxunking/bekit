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
     * 特定流程监听注解
     */
    public static final Class[] THE_FLOW_LISTEN_ANNOTATIONS = {ListenNodeDecide.class, ListenFlowException.class};
    // 监听注解与对应执行器类型Map（key：监听注解的CLass）
    private static final Map<Class, Class> LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP;

    // 初始化LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP
    static {
        LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP = new HashMap<>();
        LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP.put(ListenNodeDecide.class, NodeDecideListenExecutor.class);
        LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP.put(ListenFlowException.class, FlowExceptionListenExecutor.class);
    }

    // 被监听的流程名称
    private String flow;
    // 特定流程监听器
    private Object theFlowListener;
    // 监听执行器Map（key：监听注解的CLass）
    private Map<Class, AbstractTheFlowListenExecutor> listenExecutorMap = new HashMap<>();
    // 目标对象类型
    private Class classOfTarget = Object.class;

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
        NodeDecideListenExecutor listenExecutor = (NodeDecideListenExecutor) listenExecutorMap.get(ListenNodeDecide.class);
        if (listenExecutor != null) {
            listenExecutor.execute(theFlowListener, node, targetContext);
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
        FlowExceptionListenExecutor listenExecutor = (FlowExceptionListenExecutor) listenExecutorMap.get(ListenFlowException.class);
        if (listenExecutor != null) {
            listenExecutor.execute(theFlowListener, throwable, targetContext);
        }
    }

    /**
     * 设置监听执行器
     *
     * @param clazz          类型
     * @param listenExecutor 监听执行器
     */
    public void setListenExecutor(Class clazz, AbstractTheFlowListenExecutor listenExecutor) {
        if (!LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("特定流程监听类型" + ClassUtils.getShortName(clazz) + "不合法");
        }
        if (listenExecutor.getClass() != LISTEN_ANNOTATION_EXECUTOR_TYPE_MAP.get(clazz)) {
            throw new IllegalArgumentException("特定流程监听处理器类型" + ClassUtils.getShortName(listenExecutor.getClass()) + "不合法");
        }
        if (listenExecutorMap.containsKey(clazz)) {
            throw new IllegalStateException("特定流程监听器" + ClassUtils.getShortName(theFlowListener.getClass()) + "存在多个监听类型@" + ClassUtils.getShortName(clazz));
        }
        listenExecutorMap.put(clazz, listenExecutor);
        // 设置目标对象类型
        classOfTarget = listenExecutor.getClassOfTarget();
    }

    /**
     * 获取被监听的流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 获取目标对象类型
     */
    public Class getClassOfTarget() {
        return classOfTarget;
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
        // 校验特定流程监听器内目标对象类型是否统一
        for (AbstractTheFlowListenExecutor listenExecutor : listenExecutorMap.values()) {
            if (listenExecutor.getClassOfTarget() != classOfTarget) {
                throw new IllegalStateException("特定流程监听器" + ClassUtils.getShortName(theFlowListener.getClass()) + "内目标对象类型不统一");
            }
        }
    }

    /**
     * 节点选择事件监听执行器
     */
    public static class NodeDecideListenExecutor extends AbstractTheFlowListenExecutor {

        public NodeDecideListenExecutor(Method targetMethod, Class classOfTarget) {
            super(targetMethod, classOfTarget);
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
     * 流程异常事件监听执行器
     */
    public static class FlowExceptionListenExecutor extends AbstractTheFlowListenExecutor {

        public FlowExceptionListenExecutor(Method targetMethod, Class classOfTarget) {
            super(targetMethod, classOfTarget);
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

    /**
     * 抽象特定流程监听执行器
     */
    public static class AbstractTheFlowListenExecutor extends MethodExecutor {
        // 目标对象类型
        private Class classOfTarget;

        public AbstractTheFlowListenExecutor(Method targetMethod, Class classOfTarget) {
            super(targetMethod);
            this.classOfTarget = classOfTarget;
        }

        /**
         * 获取目标对象类型
         */
        public Class getClassOfTarget() {
            return classOfTarget;
        }
    }
}
