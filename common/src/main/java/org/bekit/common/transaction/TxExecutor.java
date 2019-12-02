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
public class TxExecutor {
    // 事务持有器
    private final ThreadLocal<Object> txStatusHolder = new ThreadLocal<>();
    // 事务管理器
    private final TransactionManager transactionManager;
    // 事务定义
    private final TransactionManager.TransactionType transactionType;

    /**
     * 创建事务
     *
     * @throws IllegalStateException 如果已存在事务
     */
    public void createTx() {
        Object txStatus = txStatusHolder.get();
        if (txStatus != null) {
            throw new IllegalStateException("事务已存在，不能同时创建多个事务");
        }
        txStatus = transactionManager.getTransaction(transactionType);
        txStatusHolder.set(txStatus);
    }

    /**
     * 提交事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void commitTx() {
        Object txStatus = txStatusHolder.get();
        if (txStatus == null) {
            throw new IllegalStateException("事务不存在，无法提交事务");
        }
        txStatusHolder.remove();
        transactionManager.commit(txStatus);
    }

    /**
     * 回滚事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void rollbackTx() {
        Object txStatus = txStatusHolder.get();
        if (txStatus == null) {
            throw new IllegalStateException("事务不存在，无法回滚事务");
        }
        txStatusHolder.remove();
        transactionManager.rollback(txStatus);
    }
}
