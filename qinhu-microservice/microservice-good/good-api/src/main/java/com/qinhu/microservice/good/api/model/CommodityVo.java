package com.qinhu.microservice.good.api.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: Commodity
 * @author: qh
 * @create: 2020-07-27 16:11
 **/
@Data
public class CommodityVo {

    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 商品类型
     */
    private String type;

    /**
     * 是否有规格 默认无
     */
    private boolean hasSpc = false;

    /**
     * 商户id
     */
    private Long owner;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 单位后缀 如： 杯、场、份、个
     */
    private String unit;

    /**
     * 规格集合
     */
    private List<SpcVo> spcVos;
}
