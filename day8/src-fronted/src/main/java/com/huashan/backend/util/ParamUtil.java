package com.huashan.backend.util;

import java.util.Map;

/**
 * 参数转换工具类
 * 安全地从Map<String, Object>中获取Integer/String等类型参数
 * 解决Jackson反序列化Map时Integer/Long/String类型不确定的问题
 */
public class ParamUtil {

    /**
     * 安全获取Integer参数
     * Jackson反序列化Map时，数字可能是Integer或Long，字符串需要解析
     */
    public static Integer getInt(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 安全获取Integer参数，带默认值
     */
    public static Integer getInt(Map<String, Object> params, String key, int defaultValue) {
        Integer result = getInt(params, key);
        return result != null ? result : defaultValue;
    }

    /**
     * 安全获取String参数
     */
    public static String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}