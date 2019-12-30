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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 公共配置
 */
@Configuration
public class CommonConfiguration {
    /**
     * 事务配置
     */
    @Configuration
    @ConditionalOnMissingBean(TransactionManager.class)
    public static class TransactionManagerConfiguration {
        /**
         * PlatformTransactionManager存在
         */
        @Configuration
        @ConditionalOnClass(PlatformTransactionManager.class)
        public static class PlatformTransactionManagerClassExists {
            /**
             * Spring事务管理器配置
             */
            @Configuration
            @ConditionalOnBean(PlatformTransactionManager.class)
            @Import(SpringTransactionManager.class)
            public static class SpringTransactionManagerConfiguration {
            }

            /**
             * 空事务管理器配置
             */
            @Configuration
            @ConditionalOnMissingBean(PlatformTransactionManager.class)
            @Import(EmptyTransactionManager.class)
            public static class EmptyTransactionManagerConfiguration {
            }
        }

        /**
         * PlatformTransactionManager不存在
         */
        @Configuration
        @ConditionalOnMissingClass("org.springframework.transaction.PlatformTransactionManager")
        @Import(EmptyTransactionManager.class)
        public static class PlatformTransactionManagerClassNotExists {
        }
    }
}
