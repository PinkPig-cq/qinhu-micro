package com.qinhu.common.core.exception;

import lombok.Getter;

/**
 * @description: 代码逻辑错误枚举
 * @author: qh
 * @create: 2020-07-09 16:09
 **/
@Getter
public enum CodeExceptionEnum implements CodeExceptionAssert{

    OBJECT_NOT_EMPTY(6001, "{0} is NULL!"),
    STRING_NOT_EMPTY(6001,"{0} is Empty!");

    CodeExceptionEnum(int code, String message) {
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
