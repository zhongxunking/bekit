/* 
 * 作者：钟勋 (e-mail:zhongxunking@163.com)
 */

/*
 * 修订记录:
 * @author 钟勋 2017-10-31 20:06 创建
 */
package org.bekit.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 枚举工具类
 */
public final class EnumUtils {
    // 驼峰命名正则表达式
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[a-z0-9][A-Z]");
    // 枚举命名正则表达式
    private static final Pattern ENUM_NAME_PATTERN = Pattern.compile("_+");

    /**
     * 获取枚举
     *
     * @param enumType      枚举类型
     * @param camelCaseName 驼峰命名
     * @return 枚举
     * @throws IllegalArgumentException 如果不存在该枚举
     */
    public static <T extends Enum<T>> T getEnum(Class<T> enumType, String camelCaseName) {
        return Enum.valueOf(enumType, toEnumName(camelCaseName));
    }

    /**
     * 驼峰命名
     *
     * @param enumObj 枚举对象
     * @return 驼峰命名
     */
    public static <T extends Enum<T>> String getCamelCaseName(T enumObj) {
        return toCamelCaseName(enumObj.name());
    }

    /**
     * 驼峰命名转枚举名
     *
     * @param camelCaseName 驼峰命名
     * @return 枚举名
     */
    public static String toEnumName(String camelCaseName) {
        if (camelCaseName == null) {
            return null;
        }

        Matcher matcher = CAMEL_CASE_PATTERN.matcher(camelCaseName);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            builder.append(camelCaseName.substring(index, matcher.start() + 1));
            builder.append('_');
            index = matcher.end() - 1;
        }
        builder.append(camelCaseName.substring(index));

        return builder.toString().toUpperCase();
    }

    /**
     * 枚举名转驼峰命名
     *
     * @param enumName 枚举名
     * @return 驼峰命名
     */
    public static String toCamelCaseName(String enumName) {
        if (enumName == null) {
            return null;
        }

        Matcher matcher = ENUM_NAME_PATTERN.matcher(enumName);
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            String part = enumName.substring(index, matcher.start());
            builder.append(toCamelCasePart(part));
            index = matcher.end();
        }
        String part = enumName.substring(index);
        builder.append(toCamelCasePart(part));
        if (builder.length() > 0) {
            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        }

        return builder.toString();
    }

    // 驼峰命名
    private static String toCamelCasePart(String part) {
        if (part == null || part.length() == 0) {
            return part;
        }
        part = part.toLowerCase();
        char[] chars = part.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
