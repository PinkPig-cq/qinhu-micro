package com.qinhu.common.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description: 条件查询
 * @author: qh
 * @create: 2019-12-16 15:26
 **/
@Data
public class WrapperQuery implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 分页
     */
    private PageQuery pageQuery;

    /**
     * 等于
     */
    private Map<String, Number> eq;

    /**
     * 小于
     */
    private Map<String, Number> lt;

    /**
     * 小于等于
     */
    private Map<String, Number> le;

    /**
     * 大于
     */
    private Map<String, Number> gt;

    /**
     * 大于等于
     */
    private Map<String, Number> ge;

    /**
     * like
     */
    private Map<String, String> like;

    /**
     * 或者
     */
    private Map<String, Object> or;

    /**
     * sql条件
     */
    private String sql;
    /**
     * in条件
     */
    private Map<String, List<Number>> in;

}
