package com.qinhu.common.core.exception;

import cn.hutool.core.util.StrUtil;

import java.util.Collection;

/**
 * @description: 断言接口  公共断言方法放这里
 * @author: qh
 * @create: 2020-05-15 15:32
 **/
public interface Assert {

    /**
     * 创建异常
     *
     * @param args
     * @return
     */
    BaseException newException(Object... args);

    /**
     * 创建异常
     *
     * @param t
     * @param args
     * @return
     */
    BaseException newException(Throwable t, Object... args);

    /**
     * 断言对象  obj  非空。如果对象  obj  为空，则抛出异常
     *
     * @param obj 待判断对象
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throw newException(obj);
        }
    }

    default void assertNotEmpty(String str) {
        if (StrUtil.isEmpty(str)) {
            throw newException(str);
        }
    }

    /**
     * 断言对象obj非空。如果对象为空，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj  待判断对象
     * @param args message占位符对应的参数列表
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }

    /**
     * 断言是个异常  直接抛出这个异常
     *
     * @param e
     */
    default void assertIsException(Throwable e) {
        if (e != null) {
            throw newException(e, null);
        }
    }

    /**
     * 断言 字符串参数不能为空
     *
     * @param params 字符串参数
     */
    default void assertNotEmpty(String... params) {
        for (String str : params) {
            if (StrUtil.isEmpty(str)) {
                throw newException(params);
            }
        }
    }

    /**
     * 断言 集合不为空和长度不为0
     *
     * @param list
     */
    default void assertCollectionNotILLEGAL(Collection list) {
        if (list == null || list.size() == 0) {
            throw newException(list);
        }
    }

    /**
     * 断言数值参数  不为0和空
     *
     * @param obj 待判断的参数
     */
    default void assertNotDefaultOrNull(Object obj) {
        double tmp;
        try {
            tmp = (double) obj;
        } catch (ClassCastException e) {
            throw newException(obj);
        }
        if ((obj == null) || (tmp == 0D)) {
            throw newException(obj);
        }
    }
}
