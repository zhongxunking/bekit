/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.common.transaction;

import lombok.AllArgsConstructor;

/**
 * 事务执行器
 */
@AllArgsConstructor
public class TransactionExecutor {
    // 事务持有器
    private final ThreadLocal<Object> transactionStatusHolder = new ThreadLocal<>();
    // 事务管理器
    private final TransactionManager transactionManager;
    // 事务定义
    private final TransactionManager.TransactionType transactionType;

    /**
     * 创建事务
     *
     * @throws IllegalStateException 如果已存在事务
     */
    public void createTransaction() {
        Object transactionStatus = transactionStatusHolder.get();
        if (transactionStatus != null) {
            throw new IllegalStateException("事务已存在，不能同时创建多个事务");
        }
        transactionStatus = transactionManager.getTransaction(transactionType);
        transactionStatusHolder.set(transactionStatus);
    }

    /**
     * 提交事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void commitTransaction() {
        Object transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new IllegalStateException("事务不存在，无法提交事务");
        }
        transactionStatusHolder.remove();
        transactionManager.commit(transactionStatus);
    }

    /**
     * 回滚事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void rollbackTransaction() {
        Object transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new IllegalStateException("事务不存在，无法回滚事务");
        }
        transactionStatusHolder.remove();
        transactionManager.rollback(transactionStatus);
    }
}
