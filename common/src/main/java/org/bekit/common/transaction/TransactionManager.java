/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-01 22:02 创建
 */
package org.bekit.common.transaction;

/**
 * 事务管理器
 */
public interface TransactionManager {
    /**
     * 获取事务
     *
     * @param type 事务类型
     * @return 事务状态（非null）
     */
    Object getTransaction(TransactionType type);

    /**
     * 提交
     *
     * @param status 事务状态
     */
    void commit(Object status);

    /**
     * 回滚
     *
     * @param status 事务状态
     */
    void rollback(Object status);

    /**
     * 事务类型
     */
    enum TransactionType {
        // 融合事务（如果已存在事务，则使用已有事务；否则创建新事务）
        REQUIRED,
        // 新事务（不管是否已存在事务，都创建新事务）
        REQUIRES_NEW
    }
}
