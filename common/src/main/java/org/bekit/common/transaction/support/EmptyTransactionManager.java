/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-02 19:27 创建
 */
package org.bekit.common.transaction.support;

import org.bekit.common.transaction.TransactionManager;

/**
 * 空事务管理器
 */
public class EmptyTransactionManager implements TransactionManager {
    @Override
    public Object getTransaction(TransactionType type) {
        return new Object();
    }

    @Override
    public void commit(Object status) {
    }

    @Override
    public void rollback(Object status) {
    }
}
