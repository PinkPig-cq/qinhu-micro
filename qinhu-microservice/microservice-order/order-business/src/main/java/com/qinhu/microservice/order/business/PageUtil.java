package com.qinhu.microservice.order.business;

import lombok.Data;
import org.apache.dubbo.common.utils.DefaultPage;
import org.apache.dubbo.common.utils.Page;

/**
 * @description: 分页工具
 * @author: qh
 * @create: 2020-07-27 23:50
 **/
@Data
public class PageUtil {

    /**
     * jpa的page转Dubbo的page
     *
     * @param jpaPage jpa Page
     * @return Dubbo Page
     */
    public static <T> Page<T> jpaPage2DubboPage(org.springframework.data.domain.Page<T> jpaPage) {

        return new DefaultPage<>(jpaPage.getNumber(), jpaPage.getSize(), jpaPage.getContent(), (int) jpaPage.getTotalElements());
    }

}
