package com.qinhu.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 最终业务异常实现
 * @author: qh
 * @create: 2020-05-15 15:37
 */
@Getter
public enum BusinessExceptionEnum implements BusinessExceptionAssert {

    /**
     * Bad licence type
     */
    BAD_LICENCE_TYPE(7001,"Bad licence type."),

    /**
     * Licence not found
     */
    LICENCE_NOT_FOUND(7002,"Licence not found."),

    /**
     * 成功
     */
    SUCCESS(200,"Success."),

    BAD_TIME(400,"时间段无效! 开始时间>结束时间&&不能为NUll"),

    COLLECTION_NOT_ILLEGAL(400,"参数集合无效!"),

    CACHE_NOT_FOUND(7002,"缓存未找到!"),

    OPEN_TS_DB_CONNECTION_ERROR(7003,"请求OpenTSDB失败"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(400,"Business not pass.");

    BusinessExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
}
