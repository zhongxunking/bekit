/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-14 21:44 创建
 */

package org.bekit.flow.annotation.processor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 节点处理器
 * <p>
 * 执行步骤：
 * 1、@Before
 * 2、@Execute（必需）
 * 3、@After
 * 4、@Error（@如果Before、@Execute、@After任何一个发生异常则执行@Error）
 * 5、@End（无论是否发生异常都会执行）
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Processor {

    /**
     * 处理器名字（默认使用被注解类的名字，首字母小写）
     */
    String name() default "";

}
