/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 19:06 创建
 */
package top.bekit.flow;

import java.util.Map;

/**
 * 流程引擎
 */
public interface FlowEngine {

    /**
     * 执行流程
     *
     * @param flow   流程名称
     * @param target 目标对象
     * @throws RuntimeException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成RuntimeException异常，目的是让客户代码不用每次调用时都需要catch
     */
    void start(String flow, Object target);

    /**
     * 执行流程
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件（为null的话则会自动生成一个空Map作为附件）
     * @throws RuntimeException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成RuntimeException异常，目的是让客户代码不用每次调用时都需要catch
     */
    void start(String flow, Object target, Map<Object, Object> attachment);

    /**
     * 以新事务插入目标对象到数据库并提交，然后执行流程
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件（为null的话则会自动生成一个空Map作为附件）
     * @throws RuntimeException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成RuntimeException异常，目的是让客户代码不用每次调用时都需要catch
     */
    void insertTargetAndStart(String flow, Object target, Map<Object, Object> attachment);

}
