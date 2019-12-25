/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-18 14:41 创建
 */
package org.bekit.flow.processor;

import org.bekit.common.registrar.AbstractRegistrar;

/**
 * 处理器注册器
 */
public class ProcessorRegistrar extends AbstractRegistrar<String, ProcessorExecutor> {
    public ProcessorRegistrar() {
        super(ProcessorExecutor::getProcessorName);
    }
}
