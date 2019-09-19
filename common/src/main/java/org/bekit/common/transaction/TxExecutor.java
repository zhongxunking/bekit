/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package org.bekit.common.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事务执行器
 */
public class TxExecutor {
    // 事务持有器
    private final ThreadLocal<TransactionStatus> txStatusHolder = new ThreadLocal<>();
    // 事务管理器
    private final PlatformTransactionManager transactionManager;
    // 事务定义
    private final TransactionDefinition txDefinition;

    public TxExecutor(PlatformTransactionManager transactionManager, boolean newTx) {
        this.transactionManager = transactionManager;
        if (newTx) {
            txDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        } else {
            txDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
        }
    }

    /**
     * 创建事务
     *
     * @throws IllegalStateException 如果已存在事务
     */
    public void createTx() {
        TransactionStatus txStatus = txStatusHolder.get();
        if (txStatus != null) {
            throw new IllegalStateException("事务已存在，不能同时创建多个事务");
        }
        txStatus = transactionManager.getTransaction(txDefinition);
        txStatusHolder.set(txStatus);
    }

    /**
     * 提交事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void commitTx() {
        TransactionStatus txStatus = txStatusHolder.get();
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
        TransactionStatus txStatus = txStatusHolder.get();
        if (txStatus == null) {
            throw new IllegalStateException("事务不存在，无法回滚事务");
        }
        txStatusHolder.remove();
        transactionManager.rollback(txStatus);
    }

    /**
     * 校验事务执行器是否有效
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (transactionManager == null) {
            throw new IllegalStateException("事务执行器内部要素不全");
        }
    }
}
