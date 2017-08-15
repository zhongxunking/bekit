/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-17 19:06 创建
 */
package org.bekit.flow;

import java.lang.reflect.UndeclaredThrowableException;
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
     * @return 流程执行结束后的目标对象（可能和传入的目标对象不是同一个对象）
     * @throws UndeclaredThrowableException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成UndeclaredThrowableException异常，
     *                                      目的是让客户代码不用每次调用时都需要catch
     */
    <T> T start(String flow, T target);

    /**
     * 执行流程
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件（为null的话则会自动生成一个空Map作为附件）
     * @return 流程执行结束后的目标对象（可能和传入的目标对象不是同一个对象）
     * @throws UndeclaredThrowableException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成UndeclaredThrowableException异常，
     *                                      目的是让客户代码不用每次调用时都需要catch
     */
    <T> T start(String flow, T target, Map<Object, Object> attachment);

    /**
     * 以新事务插入目标对象到数据库并提交事务
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件（为null的话则会自动生成一个空Map作为附件）
     * @return 插入到数据库后的目标对象（可能和传入的目标对象不是同一个对象）
     * @throws UndeclaredThrowableException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成UndeclaredThrowableException异常，
     *                                      目的是让客户代码不用每次调用时都需要catch
     */
    <T> T insertTarget(String flow, T target, Map<Object, Object> attachment);

    /**
     * 以新事务插入目标对象到数据库并提交事务，然后执行流程
     *
     * @param flow       流程名称
     * @param target     目标对象
     * @param attachment 附件（为null的话则会自动生成一个空Map作为附件）
     * @return 流程执行结束后的目标对象（可能和传入的目标对象不是同一个对象）
     * @throws UndeclaredThrowableException 执行过程中发生任何异常都会往外抛，但如果是非运行时异常则会包装成UndeclaredThrowableException异常，
     *                                      目的是让客户代码不用每次调用时都需要catch
     */
    <T> T insertTargetAndStart(String flow, T target, Map<Object, Object> attachment);

}
