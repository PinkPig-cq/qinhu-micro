package com.qinhu.microservice.good.api.command;

import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import io.eventuate.tram.commands.common.Command;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @description: 扣减库存命令
 * @author: qh
 * @create: 2020-07-09 18:35
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ReduceStoreCommand implements Command {

    @NonNull
    private List<OrderGoodsDetail> goodsDetails;
}
