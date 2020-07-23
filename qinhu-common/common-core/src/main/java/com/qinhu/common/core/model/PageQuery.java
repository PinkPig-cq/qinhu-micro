package com.qinhu.common.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 分页查询页大小的Query对象
 * @author: qinhu
 * @create: 2019-11-05 15:40
 **/

@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 3979320797076183357L;
    private Integer pageNo = 0;
    private Integer pageSize = 10;
}
