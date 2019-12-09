/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-02 19:29 创建
 */
package org.bekit.common.transaction.support;

import lombok.AllArgsConstructor;
import org.bekit.common.transaction.TransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Spring事务管理器
 */
@AllArgsConstructor
public class SpringTransactionManager implements TransactionManager {
    // 融合事务类型定义
    private static final TransactionDefinition REQUIRED_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
    // 新事务类型定义
    private static final TransactionDefinition REQUIRES_NEW_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    // 无事务类型定义
    private static final TransactionDefinition NOT_SUPPORTED_DEFINITION = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

    // 事务管理器
    private final PlatformTransactionManager transactionManager;

    @Override
    public Object getTransaction(TransactionType type) {
        TransactionDefinition definition;
        switch (type) {
            case REQUIRED:
                definition = REQUIRED_DEFINITION;
                break;
            case REQUIRES_NEW:
                definition = REQUIRES_NEW_DEFINITION;
                break;
            case NOT_SUPPORTED:
                definition = NOT_SUPPORTED_DEFINITION;
                break;
            default:
                throw new IllegalArgumentException("type不能为null");
        }
        return transactionManager.getTransaction(definition);
    }

    @Override
    public void commit(Object status) {
        transactionManager.commit((TransactionStatus) status);
    }

    @Override
    public void rollback(Object status) {
        transactionManager.rollback((TransactionStatus) status);
    }
}
