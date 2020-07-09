package com.qinhu.common.core.exception;

/**
 * @description: 代码错误
 * @author: qh
 * @create: 2020-07-09 16:07
 **/
public class CodeException extends BaseException{

    private static final long serialVersionUID = 1L;

    public CodeException(IResponseEnum responseEnum, Object[] args, String message) {
        super(responseEnum, args, message);
    }
    public CodeException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(responseEnum, args, message, cause);
    }
}
