/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-02 20:37 创建
 */
package org.bekit.common.boot;

import org.bekit.common.transaction.TransactionManager;
import org.bekit.common.transaction.support.EmptyTransactionManager;
import org.bekit.common.transaction.support.SpringTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * BekitCommon配置
 */
@Configuration
public class BekitCommonConfiguration {
    /**
     * Bekit事务配置
     */
    @Configuration
    @ConditionalOnMissingBean(TransactionManager.class)
    public static class BekitTransactionManagerConfiguration {
        // Spring事务管理器
        @Bean
        @ConditionalOnBean(PlatformTransactionManager.class)
        public TransactionManager bekitSpringTransactionManager(PlatformTransactionManager transactionManager) {
            return new SpringTransactionManager(transactionManager);
        }

        // 空事务管理器
        @Bean
        @ConditionalOnMissingBean(PlatformTransactionManager.class)
        public TransactionManager bekitEmptyTransactionManager() {
            return new EmptyTransactionManager();
        }
    }
}
