package com.liubei.yunyan.common.exception.enums;


import com.liubei.yunyan.common.exception.ErrorCode;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 *
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 * 比较特殊的是，因为之前一直使用 0 作为成功，就不使用 200 啦。
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(0, "Success");

    // ========== 服务端错误段 ==========
    ErrorCode YUN_YAN_INTERNAL_SERVER_ERROR = new ErrorCode(5000, "Failure");
    ErrorCode YUN_YAN_COMMON_QUERY_EXCEPTION = new ErrorCode(5001, "Failed to request yun yan api.response body is {}");
    ErrorCode YUN_YAN_GET_TOKEN_EXCEPTION = new ErrorCode(5002, "Failed to obtain access token.response body is {}");
    ErrorCode PROPERTIES_CONFIG_NOT_COMPLETE = new ErrorCode(4000, "YunYan properties config not complete.{}");
}
