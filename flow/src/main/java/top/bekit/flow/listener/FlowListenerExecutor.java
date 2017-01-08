/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 22:15 创建
 */
package top.bekit.flow.listener;

import org.springframework.util.ClassUtils;
import top.bekit.flow.engine.TargetContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 流程监听器执行器
 */
public class FlowListenerExecutor {
    // 被监听的流程名称
    private String flow;
    // 流程监听器
    private Object flowListener;
    // 监听选择节点执行器List
    private List<ListenMethodExecutor> listenDecideNodeExecutors = new ArrayList<ListenMethodExecutor>();

    public FlowListenerExecutor(String flow, Object flowListener) {
        this.flow = flow;
        this.flowListener = flowListener;
    }

    /**
     * 监听节点选择事件
     *
     * @param node          被选择的节点
     * @param targetContext 目标上下文
     * @throws Throwable 执行过程中发生任何异常都会往外抛
     */
    public void listenNodeDecide(String node, TargetContext targetContext) throws Throwable {
        for (ListenMethodExecutor listenMethodExecutor : listenDecideNodeExecutors) {
            if (listenMethodExecutor.isFit(node)) {
                listenMethodExecutor.execute(flowListener, node, targetContext);
            }
        }
    }

    /**
     * 添加监听选择节点执行器
     *
     * @param listenMethodExecutor 监听方法执行器
     */
    public void addListenDecideNodeExecutor(ListenMethodExecutor listenMethodExecutor) {
        listenDecideNodeExecutors.add(listenMethodExecutor);
    }

    /**
     * 校验流程监听器执行器是否有效
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (flow == null || flowListener == null) {
            throw new IllegalStateException("流程监听器" + ClassUtils.getShortName(flowListener.getClass()) + "内部要素不全");
        }
    }

    /**
     * 获取被监听的流程名称
     */
    public String getFlow() {
        return flow;
    }

    /**
     * 监听方法执行器
     */
    public static class ListenMethodExecutor {
        // 正则表达式
        private Pattern pattern;
        // 目标方法
        private Method targetMethod;
        // 是否有入参（有入参也只能是（String，TargetContext）这种形式）
        private boolean hasParameter;

        public ListenMethodExecutor(String expression, Method targetMethod, boolean hasParameter) {
            this.pattern = Pattern.compile(expression);
            this.targetMethod = targetMethod;
            this.hasParameter = hasParameter;
        }

        /**
         * 执行监听方法
         *
         * @param flowListener  流程监听器
         * @param node          节点
         * @param targetContext 目标上下文
         * @throws Throwable 执行过程中发生任何异常都会往外抛
         */
        public void execute(Object flowListener, String node, TargetContext targetContext) throws Throwable {
            try {
                if (hasParameter) {
                    targetMethod.invoke(flowListener, new Object[]{node, targetContext});
                } else {
                    targetMethod.invoke(flowListener);
                }
            } catch (InvocationTargetException e) {
                // 抛出原始异常
                throw e.getTargetException();
            }
        }

        /**
         * 节点是否适用本监听方法
         *
         * @param node 节点名称
         */
        public boolean isFit(String node) {
            return pattern.matcher(node).matches();
        }
    }
}
