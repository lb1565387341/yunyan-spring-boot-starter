package com.liubei.yunyan.service.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.XXTEA;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.liubei.yunyan.common.constants.YunYanVariable;
import com.liubei.yunyan.common.pojo.CommonResult;
import com.liubei.yunyan.common.util.YunYanUtils;
import com.liubei.yunyan.config.YunYanProperties;
import com.liubei.yunyan.service.auth.YunYanAuthManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static com.liubei.yunyan.common.exception.enums.GlobalErrorCodeConstants.YUN_YAN_COMMON_QUERY_EXCEPTION;
import static com.liubei.yunyan.common.util.ServiceExceptionUtil.exception;

public class YunYanApiClient {
    private final YunYanAuthManager authManager;
    private final XXTEA xxtea;
    private final HMac hMac;
    private final RSA rsa;

    public YunYanApiClient(YunYanAuthManager authManager) {
        this.authManager = authManager;
        xxtea = new XXTEA(authManager.getProperties().getAppSecret().getBytes());
        hMac = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, authManager.getProperties().getAppSecret().getBytes());
        rsa = new RSA(authManager.getProperties().getRsaPrivateKey(), null);
    }

    /**
     * 调用天翼视联api的统一接口
     * @param url
     * @param params
     * @param needDecrypt
     * @param typeReference
     * @return
     * @param <T>
     */
    public <T> T executeApi(String url, Map<String, Object> params, boolean needDecrypt, TypeReference<T> typeReference) {
        try (HttpResponse response = HttpUtil.createPost(authManager.getProperties().getBaseUrl() + url)
                // 1. 生成公共参数
                .form(buildPublicParams(params))
                .header(YunYanVariable.API_VERSION, authManager.getProperties().getApiVersion())
                .timeout(authManager.getProperties().getHttpTimeout()).execute()) {
            if (response.getStatus() != 200 || StrUtil.isBlank(response.body())) {
                throw exception(YUN_YAN_COMMON_QUERY_EXCEPTION, response.body());
            }
            // 不需要解密直接返回
            if (!needDecrypt) {
                CommonResult<T> result = YunYanUtils.toBean(response.body(),
                        this.convertCommonResultType(typeReference),
                        true
                );
                assert result != null;
                return result.getCheckedData();
            } else {
                CommonResult<String> result = YunYanUtils.toBean(response.body(),
                        new TypeReference<CommonResult<String>>() {},
                        true
                );
                assert result != null;
                return xxteaDecryptDataToBean(result.getCheckedData(), typeReference);
            }
        }
    }

    /**
     * xxtea 解密数据
     */
    public <T> T xxteaDecryptDataToBean(String encryptData, TypeReference<T> typeReference) {
        return JSONUtil.toBean(xxtea.decryptStr(encryptData), typeReference, true);
    }

    /**
     * 私钥 RSA 解密数据
     */
    public <T> T rsaDecryptDataToBean(String encryptData, TypeReference<T> typeReference) {
        return JSONUtil.toBean(rsa.decryptStr(encryptData, KeyType.PrivateKey), typeReference, true);
    }

    private <T> TypeReference<CommonResult<T>> convertCommonResultType(TypeReference<T> typeReference) {
        // 获取原始类型 T
        Type originalType = typeReference.getType();
        // 构造 CommonResult<T> 的参数化类型
        ParameterizedType commonResultType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{originalType};
            }
            @Override
            public Type getRawType() {
                return CommonResult.class;
            }
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        // 创建新的 TypeReference 实例
        return new TypeReference<CommonResult<T>>() {
            @Override
            public Type getType() {
                return commonResultType;
            }
        };
    }

    private Map<String, Object> buildPublicParams(Map<String, Object> paramMap) {
        YunYanProperties properties = authManager.getProperties();
        if (CollUtil.isEmpty(paramMap)) paramMap = Maps.newHashMap();
        paramMap.put(YunYanVariable.ACCESS_TOKEN, authManager.getAccessToken());
        paramMap.put(YunYanVariable.ENTERPRISE_USER, properties.getEnterpriseUser());
        Long timestamp =  System.currentTimeMillis();
        String params = xxtea.encryptHex(YunYanUtils.mapToString(paramMap));
        String signature = hMac.digestHex(properties.getAppId() + properties.getClientType() + params + timestamp + properties.getVersion());
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(YunYanVariable.APP_ID, properties.getAppId());
        resultMap.put(YunYanVariable.VERSION, properties.getVersion());
        resultMap.put(YunYanVariable.CLIENT_TYPE, properties.getClientType());
        resultMap.put(YunYanVariable.TIMESTAMP_KEY, timestamp);
        resultMap.put(YunYanVariable.SIGNATURE, signature);
        resultMap.put(YunYanVariable.PARAMS, params);
        return resultMap;
    }
}
