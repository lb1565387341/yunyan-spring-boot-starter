package com.liubei.yunyan.service.auth;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.XXTEA;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.liubei.yunyan.common.constants.YunYanApi;
import com.liubei.yunyan.common.constants.YunYanVariable;
import com.liubei.yunyan.common.exception.ErrorCode;
import com.liubei.yunyan.common.pojo.CommonResult;
import com.liubei.yunyan.common.util.YunYanUtils;
import com.liubei.yunyan.config.YunYanProperties;
import com.liubei.yunyan.service.auth.dto.AccessTokenResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.liubei.yunyan.common.exception.enums.GlobalErrorCodeConstants.YUN_YAN_COMMON_QUERY_EXCEPTION;
import static com.liubei.yunyan.common.exception.enums.GlobalErrorCodeConstants.YUN_YAN_GET_TOKEN_EXCEPTION;
import static com.liubei.yunyan.common.exception.enums.GlobalErrorCodeConstants.YUN_YAN_INTERNAL_SERVER_ERROR;
import static com.liubei.yunyan.common.util.ServiceExceptionUtil.exception;

@Slf4j
public class DefaultYunYanAuthManager implements YunYanAuthManager {
    private final YunYanProperties properties;
    private final LoadingCache<String, String> tokenCache;

    public DefaultYunYanAuthManager(YunYanProperties properties) {
        this.properties = properties;
        this.tokenCache = CacheBuilder.newBuilder()
                .expireAfterWrite(properties.getTokenExpireMargin(), TimeUnit.HOURS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return refreshTokenInternal();
                    }
                });
    }

    @Override
    public String getAccessToken() {
        try {
            return tokenCache.get("YUN_YAN_SINGLE_TOKEN");
        } catch (Exception e) {
            throw exception(new ErrorCode(YUN_YAN_INTERNAL_SERVER_ERROR.getCode(), e.getMessage()));
        }
    }

    @Override
    public void refreshToken() {
        tokenCache.invalidateAll();
    }

    private String refreshTokenInternal() {
        // 实现具体的token获取逻辑
        // 调用天翼视联的认证接口
        Long timestamp =  System.currentTimeMillis();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put(YunYanVariable.GRANT_TYPE, "vcp_189");
        XXTEA xxtea = new XXTEA(properties.getAppSecret().getBytes());
        String params = xxtea.encryptHex(YunYanUtils.mapToString(paramMap));
        HMac hMac = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, properties.getAppSecret().getBytes());
        String signature = hMac.digestHex(properties.getAppId() + properties.getClientType() + params + timestamp + properties.getVersion());
        // 返回全部参数
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(YunYanVariable.APP_ID, properties.getAppId());
        resultMap.put(YunYanVariable.VERSION, properties.getVersion());
        resultMap.put(YunYanVariable.CLIENT_TYPE, properties.getClientType());
        resultMap.put(YunYanVariable.TIMESTAMP_KEY, timestamp);
        resultMap.put(YunYanVariable.SIGNATURE, signature);
        resultMap.put(YunYanVariable.PARAMS, params);
        try (HttpResponse response = HttpUtil.createPost(properties.getBaseUrl() + YunYanApi.GET_ACCESS_TOKEN_URI)
                .form(resultMap)
                .header(YunYanVariable.API_VERSION, properties.getApiVersion())
                .timeout(properties.getHttpTimeout()).execute()) {
            if (response.getStatus() != 200 || CharSequenceUtil.isBlank(response.body())) {
                throw exception(YUN_YAN_GET_TOKEN_EXCEPTION, response.body());
            }
            CommonResult<AccessTokenResponseDTO> result = YunYanUtils.toBean(response.body(),
                    new TypeReference<CommonResult<AccessTokenResponseDTO>>() {},
                    true
            );
            return this.checkResult(result, response.body()).getAccessToken();
        }
    }

    private <T> T checkResult(CommonResult<T> result, String responseBody) {
        // 解决白名单失败时，返回大写CODE转换失败的问题
        if (ObjectUtil.isNull(result) || ObjectUtil.isNull(result.getCode())) {
            throw exception(YUN_YAN_COMMON_QUERY_EXCEPTION, responseBody);
        }
        return result.getCheckedData();
    }

    @Override
    public YunYanProperties getProperties() {
        return this.properties;
    }
}
