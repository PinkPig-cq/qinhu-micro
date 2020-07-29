package com.qinhu.microservice.order.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.qinhu.common.core.model.BaseResponse;
import com.qinhu.common.core.model.PageQuery;
import com.qinhu.common.core.model.WrapperQuery;
import com.qinhu.microservice.order.api.model.OrderPayStatus;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.OrderVo;
import com.qinhu.microservice.order.api.model.PaymentName;
import com.qinhu.microservice.order.api.model.query.ChangeOrderQuery;
import com.qinhu.microservice.order.business.OrderServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.Page;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class OrderServiceRpcImplTest extends OrderServiceTest {

    private OrderVo orderVo = new OrderVo();

    private WrapperQuery wrapperQuery = new WrapperQuery();

    @PostConstruct
    public void init() {
        orderVo = super.test();
        for (int i = 0; i < 6; i++) {
            super.test();
        }

        //初始化条件
        WrapperQuery wrapperQuery = new WrapperQuery();
        Map<String, Number> eq = new HashMap<>(1);
        eq.put("sellerId", 1L);

        Map<String, Object> gt = new HashMap<>(1);
        gt.put("createTime", DateUtil.date(new Date()).offset(DateField.MINUTE, -10));

        Map<String, Object> lt = new HashMap<>(1);
        lt.put("createTime", DateUtil.date(new Date()).offset(DateField.MINUTE, 10));
        Map<String, WrapperQuery.Between> between = new HashMap<>(1);
        between.put("createTime", new WrapperQuery.Between<>(DateUtil.date(new Date()).offset(DateField.DAY_OF_MONTH, -1),
                DateUtil.date(new Date()).offset(DateField.DAY_OF_MONTH, 1)));
        wrapperQuery.setEq(eq);
        wrapperQuery.setBetween(between);
        this.wrapperQuery = wrapperQuery;
    }

    @Test
    void createOrder() {
        super.test();
    }

    @Test
    void editOrder() {
        ChangeOrderQuery changeOrderQuery = new ChangeOrderQuery();
        BeanUtil.copyProperties(orderVo, changeOrderQuery);
        changeOrderQuery.setOrderStatus(OrderStatus.CONFIRM);
        BaseResponse<OrderVo> orderVoBaseResponse = super.orderServiceRpc.editOrder(changeOrderQuery);
        orderVo = orderVoBaseResponse.getData();
        log(orderVo, "editOrder");
    }

    @Test
    void countOrdersByDate() {
        BaseResponse<Long> longBaseResponse = super.orderServiceRpc.countOrdersByDate(DateUtil.date(new Date()).offset(DateField.MINUTE, -10), DateUtil.date(new Date()).offset(DateField.MINUTE, 10), OrderStatus.WAIT_PAY, "1");
        Long data = longBaseResponse.getData();
        log(data, "countOrdersByDate");
    }

    @Test
    void getListComplex() {


        BaseResponse<List<OrderVo>> listComplex = super.orderServiceRpc.getListComplex(wrapperQuery);
        List<OrderVo> data = listComplex.getData();


    }

    @Test
    void page() {

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNo(1);
        pageQuery.setPageSize(5);
        wrapperQuery.setPageQuery(pageQuery);


        BaseResponse<Page<OrderVo>> page = super.orderServiceRpc.page(wrapperQuery);
        Page<OrderVo> data = page.getData();
    }

    @Test
    void updateBatch() {
        BaseResponse<List<OrderVo>> listComplex = super.orderServiceRpc.getListComplex(wrapperQuery);
        List<OrderVo> data = listComplex.getData();

        List<ChangeOrderQuery> queryList = new ArrayList<>(2);
        ChangeOrderQuery changeOrderQuery1 = new ChangeOrderQuery();
        BeanUtil.copyProperties(data.get(0), changeOrderQuery1);
        changeOrderQuery1.setOrderStatus(OrderStatus.WAIT_DELIVERING);
        changeOrderQuery1.setPaymentMethodName(PaymentName.WeChatPay);
        changeOrderQuery1.setPayStatus(OrderPayStatus.PAY_YES);
        ChangeOrderQuery changeOrderQuery2 = new ChangeOrderQuery();
        BeanUtil.copyProperties(data.get(0), changeOrderQuery2);
        changeOrderQuery2.setOrderStatus(OrderStatus.WAIT_DELIVERING);
        changeOrderQuery2.setPaymentMethodName(PaymentName.WeChatPay);
        changeOrderQuery2.setPayStatus(OrderPayStatus.PAY_YES);
        queryList.add(changeOrderQuery1);
        queryList.add(changeOrderQuery2);
        boolean b = super.orderServiceRpc.updateBatch(queryList);

        BaseResponse<List<OrderVo>> listComplex2 = super.orderServiceRpc.getListComplex(null);
        List<OrderVo> data2 = listComplex2.getData();
    }

    @Test
    void topStatistics() {
        updateBatch();
        BaseResponse<Map<String, Double>> mapBaseResponse = super.orderServiceRpc.topStatistics(8, "", DateUtil.date(new Date()).offset(DateField.DAY_OF_MONTH, -1),
                DateUtil.date(new Date()).offset(DateField.DAY_OF_MONTH, 1));
        Map<String, Double> data = mapBaseResponse.getData();

    }

    @Test
    void tradeStatistics() {

    }

    @Test
    void countByPayMethodStatistics() {
    }

    @Test
    void moneyByPayMethodStatistics() {
    }

    @Test
    void everyDayWorkingCapital() {
    }

    @Test
    void statisticsGoodsMarketMoneyGroupSellerGood() {
    }

    @Test
    void getCount() {
    }

    @Test
    void confirm() {
    }

    private void log(Object obj, String methodName) {
        log.info("=====================start {} =====================", methodName);
        log.info(JSON.toJSONString(obj));
        log.info("=====================end {} =======================", methodName);
    }
}