package com.qinhu.microservice.order.business.service.strategy;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.qinhu.microservice.order.api.model.OrderPayStatus;
import com.qinhu.microservice.order.api.model.OrderStatus;
import com.qinhu.microservice.order.api.model.PaymentName;
import com.qinhu.microservice.order.business.domain.Order;
import com.qinhu.microservice.order.business.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @description: 订单统计策略公共部分
 * @author: qh
 * @create: 2020-01-02 16:38
 **/
@Component
public class OrderStatisticsStraategyAbstract implements IOrderStatisticsStrategy {

    OrderRepository orderRepository;

    /**
     * 订单集合缓存 防止一次请求同一策略多次走数据库
     */
    protected List<Order> orders;

    /**
     * 锁
     */
    private ReentrantLock lock = new ReentrantLock();


    public OrderStatisticsStraategyAbstract(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        initMap();
    }

    /**
     * 查询条件 支付状态 已支付
     */
    protected Map<String, String> PayYESCondition;

    /**
     * 查询条件 订单状态 已退款
     */
    protected Map<String, String> OrderStatusCondition;

    /**
     * 初始化时添加  支付状态 为PAY_YES
     */
    private void initMap() {
        PayYESCondition = new HashMap<>(1);
        PayYESCondition.put(OrderDBColumn.PAY_STATUS, OrderPayStatus.PAY_YES.getName());
        OrderStatusCondition = new HashMap<>(1);
        OrderStatusCondition.put(OrderDBColumn.ORDER_STATUS, OrderStatus.REFUND.getName());
    }

    @Override
    public Map<String, BigDecimal> statisticsMoneyByPayMenthod(String shopName, long start, long end) {
        try {
            List<Order> list = getList(shopName, start, end, PayYESCondition);
            Map<String, BigDecimal> map = new LinkedHashMap<>();
            list.forEach(arg -> {
                String key = arg.getPaymentMethodName().getName();
                if (map.containsKey(key)) {
                    map.put(key, map.get(key).add(arg.getPayPrice()));
                } else {
                    map.put(key, arg.getPayPrice());
                }
            });
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> statisticsCountByPayMenthod(String shopName, long start, long end) {
        try {
            List<Order> list = getList(shopName, start, end, PayYESCondition);
            Map<String, Integer> map = new LinkedHashMap<>();
            list.forEach(arg -> {
                String key = arg.getPaymentMethodName().getName();
                if (map.containsKey(key)) {
                    map.put(key, map.get(key) + 1);
                } else {
                    map.put(key, 1);
                }
            });

            return map;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public BigDecimal statisticsTradeMoney(String shopName, long start, long end) {
        return statisticsMoney(shopName, start, end, PayYESCondition);
    }

    @Override
    public Integer statisticsTradeCount(String shopName, long start, long end) {
        return statisticsCount(shopName, start, end, PayYESCondition);
    }

    @Override
    public BigDecimal statisticsRefundMoney(String shopName, long start, long end) {
        return statisticsMoney(shopName, start, end, OrderStatusCondition);
    }

    @Override
    public Integer statisticsRefundCount(String shopName, long start, long end) {
        return statisticsCount(shopName, start, end, OrderStatusCondition);
    }

    @Override
    public Map<String, BigDecimal> statisticsTradeMoneyChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, Integer> statisticsTradeCountChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, Integer> statisticsRefundCountChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> statisticsRefundMoneyChart(String shopName, long start, long end) {
        return null;
    }

    @Override
    public List<Order> statisticsList(String shopName, long start, long end, int payment) {
        return null;
    }

    /**
     * 统计金额
     *
     * @param shopName  店铺名
     * @param start     起始
     * @param end       结束
     * @param condition 额外条件
     * @return BigDecimal
     */
    protected BigDecimal statisticsMoney(String shopName, long start, long end, Map<String, String> condition) {
        try {
            //统计所有订单总额
            List<Order> list = getList(shopName, start, end, condition);
            AtomicReference<BigDecimal> atomTotalMoney = new AtomicReference<>();
            BigDecimal totalMoney = new BigDecimal(0);
            atomTotalMoney.set(totalMoney);
            list.forEach(arg ->
                    atomTotalMoney.compareAndSet(atomTotalMoney.get(),
                            atomTotalMoney.get().add(arg.getPayPrice()).setScale(2, RoundingMode.HALF_DOWN))
            );
            return atomTotalMoney.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 统计金额
     *
     * @param shopName  店铺名
     * @param start     起始
     * @param end       结束
     * @param condition 额外条件
     * @return Integer
     */
    protected Integer statisticsCount(String shopName, long start, long end, Map<String, String> condition) {
        try {
            //统计所有订单笔数
            List<Order> list = getList(shopName, start, end, condition);
            AtomicInteger totalCount = new AtomicInteger();
            list.forEach(arg -> totalCount.addAndGet(1));
            return totalCount.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据店铺名和订单完成时间段查询订单集合
     *
     * @param shopName 店铺名
     * @param start    起始时间
     * @param end      结束时间
     * @param status   状态：订单状态  支付状态
     * @return List<Orders>
     */
    protected List<Order> getList(String shopName, long start, long end,
                                  Map<String, String> status) throws Exception {

        if (!this.checkCondition(start, end)) {
            throw new Exception("参数异常");
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "updateTime");
        Specification<Order> specification = (Specification<Order>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.between(root.get("updateTime"), DateUtil.date(start), DateUtil.date(end));
            if (StrUtil.isNotEmpty(shopName)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("sellerId"), shopName));
            }
            if (status != null && status.size() != 0) {
                for (String key : status.keySet()) {
                    //如果以支付为条件
                    if (OrderDBColumn.PAY_STATUS.equals(key)) {
                        OrderPayStatus orderPayStatus = OrderPayStatus.initByName(status.get(key));
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(key), orderPayStatus));
                    }
                    //如果以订单状态为条件
                    if (OrderDBColumn.ORDER_STATUS.equals(key)) {
                        OrderStatus orderStatus = OrderStatus.initByName(status.get(key));
                        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(key), orderStatus));
                    }
                }
            }
            return predicate;
        };
        try {
            return orderRepository.findAll(specification, sort);
        } catch (Exception ex) {
            throw new Exception("查询异常");
        }
    }

    /**
     * 初始化  金额统计Map
     *
     * @param capacity 容量
     * @return HashMap
     */
    protected Map<String, BigDecimal> initMoneyMap(int capacity) {
        return new HashMap<String, BigDecimal>(capacity) {
            {
                for (int i = 1; i <= capacity; i++) {
                    put(i + "", new BigDecimal(0));
                }
            }
        };
    }

    /**
     * 初始化  笔数统计Map
     *
     * @param capacity 容量
     * @return HashMap
     */
    protected Map<String, Integer> initCountMap(int capacity) {
        return new HashMap<String,Integer>(capacity) {
            {
                for (int i = 1; i <= capacity; i++) {
                    put(i + "", 0);
                }
            }
        };
    }

    /**
     * 验证参数是否有效
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return boolean
     */
    private boolean checkCondition(long start, long end) {

        //验证时间
        return start >= 1 && end >= 1 && start < end;
    }

    /**
     * 选择支付方式
     *
     * @param paymentStrategy 支付方式选择  0:微信  1:支付宝
     * @return String
     */
    protected String getPaymentName(int paymentStrategy) {
        switch (paymentStrategy) {
            case 1:
                return PaymentName.AliPay.name();
            case 2:
                return PaymentName.CMBPay.name();
            default:
                return PaymentName.WeChatPay.name();
        }
    }

    /**
     * 数据库
     */
    protected List<Order> getListOnce(String shopName, long start, long end, Map<String, String> codition) {
        try {
            //为空的时候走已支付 包括退款和完成订单
            lock.lock();
            if (orders == null) {
                orders = this.getList(shopName, start, end, PayYESCondition);
            }
            lock.unlock();
            //当选择为退款时
            if (codition.containsKey(OrdersDBColumn.ORDER_STATUS)) {
                return orders.stream()
                        .filter(arg -> OrderStatus.REFUND.equals(arg.getOrderStatus()))
                        .collect(Collectors.toList());
            }
            return orders;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * map按照key排序
     *
     * @param waitSortMap 待排序map
     * @return Map
     */
    protected Map sortByKey(Map waitSortMap) {
        Comparator<String> comparable = (o1, o2) -> {
            int key1 = Integer.parseInt(StrUtil.isEmpty(o1) ? "0" : o1);
            int key2 = Integer.parseInt(StrUtil.isEmpty(o2) ? "0" : o2);
            if (key1 == key2) {
                return 1;
            }
            return key1 - key2;
        };
        return MapUtil.sort(waitSortMap, comparable);
    }
}
