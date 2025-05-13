package com.liubei.yunyan.common.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 天翼视联工具类
 */
public class YunYanUtils {

    public static <T> T toBean(String jsonString, Type beanType, boolean ignoreError) {
        final JSON json = JSONUtil.parse(jsonString, JSONConfig.create()
                .setIgnoreError(ignoreError));
        if(null == json){
            return null;
        }
        return json.toBean(beanType);
    }

    public static String mapToString(Map<String, Object> map) {
        return map.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
