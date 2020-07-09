package com.qinhu.common.core.exception;

import java.text.MessageFormat;

/**
 * @description: 代码逻辑错误断言接口
 * @author: qh
 * @create: 2020-07-09 16:05
 **/
public interface CodeExceptionAssert extends Assert, IResponseEnum {

    @Override
    default BaseException newException(Object... args) {

        String msg = MessageFormat.format(this.getMessage(), args);

        return new BusinessException(this, args, msg);
    }


    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);

        return new BusinessException(this, args, msg, t);
    }

}
