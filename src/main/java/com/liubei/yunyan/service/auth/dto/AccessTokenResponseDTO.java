package com.liubei.yunyan.service.auth.dto;

import lombok.Data;

/**
 * 访问token获取的结果
 * 参考 https://open.ctseelink.cn/portal/document-open/detail?sid=VCG25BzQiuGcom7f1871Vq9MrrAXq6qTN1DQ25FSjzo&useFlag=doc
 */
@Data
public class AccessTokenResponseDTO {
    /**
     * 访问token
     */
    private String accessToken;
    /**
     * 刷新token
     */
    private String refreshToken;
    /**
     * token过期时间
     */
    private Integer expiresIn;
    /**
     * refreshToken过期时间
     */
    private Integer refreshExpiresIn;
}
