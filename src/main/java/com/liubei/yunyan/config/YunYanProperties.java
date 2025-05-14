package com.liubei.yunyan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "yun-yan")
public class YunYanProperties {
    /**
     * 是否启用天翼视联
     */
    private boolean enabled;
    /**
     * 天翼视联基本url
     */
    private String baseUrl = "https://vcp.21cn.com";
    /**
     * appId不为空
     */
    private String appId;
    /**
     * apiKey不为空
     */
    private String apiKey;
    /**
     * secretKey不为空
     */
    private String appSecret;
    /**
     * RSA私钥不为空
     */
    private String rsaPrivateKey;
    /**
     * enterpriseUser不为空
     */
    private String enterpriseUser;
    /**
     * http请求超时时间，默认10秒
     */
    private int httpTimeout = 10 * 1000;
    /**
     * token过期时间不为空 单位小时。默认3天过期
     */
    private int tokenExpireMargin = 3 * 24;
    /**
     * 服务端版本号，若公共参数version为v1.0，返回敏感参数使用XXTea加密
     */
    private String version = "v1.0";
    /**
     * api version 传2.0
     */
    private String apiVersion = "2.0";
    /**
     * 接入端类型 , 可选值：
     * 0-IOS
     * 1-Android
     * 2-Web/WAP/H5
     * 3-PC
     * 4-服务端
     */
    private Integer clientType = 3;
}
