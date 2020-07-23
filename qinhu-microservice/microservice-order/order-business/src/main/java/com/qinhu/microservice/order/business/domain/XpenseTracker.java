package com.qinhu.microservice.order.business.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author qinhu
 * @since 2020-02-10
 */
@Data
@Entity
public class XpenseTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private Long userId;

    private String orderNo;

    private Long createTime;

    private Integer num;
}
