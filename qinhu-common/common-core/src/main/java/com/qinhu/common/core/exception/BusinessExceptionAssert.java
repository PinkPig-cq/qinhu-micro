package com.qinhu.common.core.exception;

import java.text.MessageFormat;

/**
 * @description:  业务异常与断言结合的接口  适配器
 * @author: qh
 * @create: 2020-05-15 15:36
 **/
public interface BusinessExceptionAssert extends IResponseEnum, Assert {

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
