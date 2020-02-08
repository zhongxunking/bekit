/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2019-12-02 20:37 创建
 */
package org.bekit.common.boot;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 公共自动配置
 */
@Configuration
@AutoConfigureAfter(TransactionAutoConfiguration.class)
@Import(CommonConfiguration.class)
public class CommonAutoConfiguration {
}
