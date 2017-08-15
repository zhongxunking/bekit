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
    // 事务定义（传播行为是REQUIRES_NEW，即每次都开启一个新事务）
    private static final TransactionDefinition TX_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

    // 事务管理器
    private PlatformTransactionManager txManager;
    // 事务持有器
    private ThreadLocal<TransactionStatus> txStatusHolder = new ThreadLocal<>();

    public TxExecutor(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    /**
     * 创建事务
     *
     * @throws IllegalStateException 如果已存在事务
     */
    public void createTx() {
        if (txStatusHolder.get() != null) {
            throw new IllegalStateException("本线程事务已存在，不能同时创建多个事务");
        }
        txStatusHolder.set(txManager.getTransaction(TX_DEFINITION));
    }

    /**
     * 提交事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void commitTx() {
        if (txStatusHolder.get() == null) {
            throw new IllegalStateException("事务不存在，无法提交事务");
        }
        txManager.commit(txStatusHolder.get());
        txStatusHolder.remove();
    }

    /**
     * 回滚事务
     *
     * @throws IllegalStateException 如果不存在事务
     */
    public void rollbackTx() {
        if (txStatusHolder.get() == null) {
            throw new IllegalStateException("事务不存在，无法回滚事务");
        }
        txManager.rollback(txStatusHolder.get());
        txStatusHolder.remove();
    }

    /**
     * 校验事务执行器是否有效
     *
     * @throws IllegalStateException 如果校验不通过
     */
    public void validate() {
        if (txManager == null) {
            throw new IllegalStateException("事务执行器内部要素不全");
        }
    }
}
