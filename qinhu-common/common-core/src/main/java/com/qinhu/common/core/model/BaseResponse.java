package com.qinhu.common.core.model;

import com.qinhu.common.core.exception.BusinessExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 统一返回参数实体
 * @author: qh
 * @create: 2020-05-18 10:49
 **/
@Data
@AllArgsConstructor
public class BaseResponse<T> {

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应提示信息
     */
    private String msg;

    BaseResponse() {

    }

    /**
     * 200,带提示信息
     *
     * @param msg 提示信息
     * @param <T> 响应数据
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> okMsg(String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMsg(msg);
        baseResponse.setCode(BusinessExceptionEnum.SUCCESS.getCode());
        baseResponse.setData(null);
        return baseResponse;
    }

    /**
     * 200,带默认提示信息
     *
     * @param <T> 响应数据
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> okData(T t) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMsg("操作成功!");
        baseResponse.setCode(BusinessExceptionEnum.SUCCESS.getCode());
        baseResponse.setData(t);
        return baseResponse;
    }

    /**
     * 200,带默认提示信息和响应数据
     *
     * @param t   响应数据
     * @param msg 提示信息
     * @param <T> 响应数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> okDataMsg(T t, String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMsg(msg);
        baseResponse.setCode(BusinessExceptionEnum.SUCCESS.getCode());
        baseResponse.setData(t);
        return baseResponse;
    }

    /**
     * 400,带提示信息
     *
     * @param msg 提示信息
     * @param <T> 响应数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> errorMsg(String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMsg(msg);
        baseResponse.setCode(BusinessExceptionEnum.BUSINESS_ERROR.getCode());
        baseResponse.setData(null);
        return baseResponse;
    }

}
