/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2016-12-16 01:14 创建
 */
package top.bekit.service.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ClassUtils;
import top.bekit.service.annotation.service.Service;
import top.bekit.service.engine.ServiceContext;
import top.bekit.service.service.ServiceExecutor.ServiceMethodExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 服务解析器
 */
public class ServiceParser {

    /**
     * 解析服务
     *
     * @param service   服务
     * @param txManager 事务管理器
     * @return 服务执行器
     */
    public static ServiceExecutor parseService(Object service, PlatformTransactionManager txManager) {
        Service serviceAnnotation = service.getClass().getAnnotation(Service.class);
        // 获取服务名称
        String serviceName = serviceAnnotation.name();
        if (StringUtils.isEmpty(serviceName)) {
            serviceName = ClassUtils.getShortNameAsProperty(service.getClass());
        }
        // 创建服务执行器
        ServiceExecutor serviceExecutor = new ServiceExecutor(serviceName, serviceAnnotation.enableTx(), service);
        if (serviceAnnotation.enableTx()) {
            if (txManager == null) {
                throw new IllegalArgumentException("服务" + serviceAnnotation.name() + "的enableTx属性为开启状态，但不存在事务管理器（PlatformTransactionManager），请检查是否有配置spring事务管理器");
            }
            serviceExecutor.setTxManager(txManager);
        }
        for (Method method : service.getClass().getDeclaredMethods()) {
            for (Class clazz : ServiceExecutor.SERVICE_METHOD_ANNOTATIONS) {
                if (method.isAnnotationPresent(clazz)) {
                    // 设置服务方法执行器
                    serviceExecutor.setMethodExecutor(clazz, parseServiceMethod(method));
                    break;
                }
            }
        }
        serviceExecutor.validate();

        return serviceExecutor;
    }

    // 解析服务方法
    private static ServiceMethodExecutor parseServiceMethod(Method method) {
        // 校验方法类型
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("服务方法" + ClassUtils.getQualifiedMethodName(method) + "必须是public类型");
        }
        // 校验入参
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1 || parameterTypes[0] != ServiceContext.class) {
            throw new IllegalArgumentException("服务方法" + ClassUtils.getQualifiedMethodName(method) + "的入参必须是（ServiceContext）");
        }
        // 校验返回类型
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("服务方法" + ClassUtils.getQualifiedMethodName(method) + "的返回类型必须是void");
        }

        return new ServiceMethodExecutor(method);
    }
}
