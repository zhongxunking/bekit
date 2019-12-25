/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-19 01:26 创建
 */
package org.bekit.common.registrar;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 抽象注册器
 */
@AllArgsConstructor
public abstract class AbstractRegistrar<N, E> {
    // 元素集
    private final Map<N, E> elementMap = new ConcurrentHashMap<>();
    // 名称提取器
    private final Function<E, N> nameExtractor;

    /**
     * 注册元素
     *
     * @param element 被注册的元素
     * @return 被替换的元素
     */
    public E register(E element) {
        return elementMap.put(nameExtractor.apply(element), element);
    }

    /**
     * 获取所有元素的名称
     *
     * @return 所有所有元素的名称
     */
    public Set<N> getNames() {
        return Collections.unmodifiableSet(elementMap.keySet());
    }

    /**
     * 获取元素
     *
     * @param name 元素名称
     * @return 元素
     */
    public E get(N name) {
        return elementMap.get(name);
    }
}
