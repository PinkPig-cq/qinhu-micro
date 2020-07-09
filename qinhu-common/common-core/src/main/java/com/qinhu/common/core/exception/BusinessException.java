package com.qinhu.common.core.exception;

/**
 * @description: 业务方面的异常
 * @author: qh
 * @create: 2020-05-15 15:28
 **/
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(IResponseEnum responseEnum, Object[] args, String message) {
        super(responseEnum, args, message);
    }

    public BusinessException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(responseEnum, args, message, cause);
    }
}
