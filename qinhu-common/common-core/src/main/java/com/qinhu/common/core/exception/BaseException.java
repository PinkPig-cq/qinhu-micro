package com.qinhu.common.core.exception;

/**
 * @description: 基础异常
 * @author: qh
 * @create: 2020-05-15 15:27
 **/
public class BaseException extends RuntimeException {

    protected static final long serialVersionUID = 1L;

    private int code;

    private String message;

    private Object[] args;

    public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
        this.message = message;
        this.args = args;
    }

    public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.code = responseEnum.getCode();
        this.message = message;
        this.args = args;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
