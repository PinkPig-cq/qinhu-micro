package com.qinhu.microservice.good.api.command;

import com.qinhu.microservice.order.api.model.query.OrderGoodsDetail;
import io.eventuate.tram.commands.common.Command;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @description: 添加Good库存, ReduceStoreCommand的补偿命令
 * @author: qh
 * @create: 2020-07-10 14:18
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AddStoreCommand implements Command {

    @NonNull
    private List<OrderGoodsDetail> goodsDetails;
}
