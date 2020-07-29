package com.qinhu.common.core.exception;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @description: 业务异常与断言结合的接口  适配器
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

    /**
     * 断言对象  时间段合法
     *
     * @param start 开始时间
     * @param end   结束时间
     */
    default void assertTimeVail(Long start, Long end) {
        if (end == null || start == null) {
            throw newException(start, end);
        }
        if (end == 0 && start == 0) {
            throw newException(start, end);
        }
        if (start > end) {
            throw newException(start, end);
        }
        if (start < 0 || end < 0) {
            throw newException(start, end);
        }
    }

    /**
     * 断言对象  时间段合法
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    default void assertTimeVail(Date startDate, Date endDate) {
        Long start = startDate.getTime();
        Long end = endDate.getTime();
        assertTimeVail(start,end);
    }
}
