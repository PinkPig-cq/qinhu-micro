package com.qinhu.common.core.until;

import cn.hutool.core.lang.Snowflake;

/**
 * @description: 雪花ID工具类
 * @author: qh
 * @create: 2020-07-06 10:24
 **/
public class SnowflakeIdUtil {


    /**
     * 获取全局唯一序列
     *
     * @return 唯一串
     */
    public static String getSnowId() {
        Snowflake snowflake = new Snowflake(1L, 1L);
        return snowflake.nextIdStr();
    }

}
