package com.qinhu.microservice.order.business;

import com.qinhu.common.core.model.WrapperQuery;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @description: 分页条件查询 解析工具类
 * @author: qh
 * @create: 2020-01-08 22:11
 **/
public class WrapperQueryJpaResolver<T> {

    /**
     * 组装查询条件
     *
     * @param query
     * @return
     */
    public Specification<T> getQueryWrapper(WrapperQuery query) {


        return (root, crQuery, criteriaBuilder) -> {

            //最终sql语句 构造恒成立条件
            Predicate and = criteriaBuilder.conjunction();

            //拼装等于条件
            if (query.getEq() != null) {
                for (String key : query.getEq().keySet()) {
                    Path<Object> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.equal(objectPath, query.getEq().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }
            //拼装小于条件
            if (query.getLt() != null) {
                for (String key : query.getLt().keySet()) {
                    Path<Number> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.lt(objectPath, query.getLt().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }

            //拼装小于等于条件
            if (query.getLe() != null) {
                for (String key : query.getLe().keySet()) {
                    Path<Number> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.le(objectPath, query.getLe().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }
            //拼装大于于条件
            if (query.getGt() != null) {
                for (String key : query.getGt().keySet()) {
                    Path<Number> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.gt(objectPath, query.getGt().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }

            //拼装大于等于条件
            if (query.getGe() != null) {
                for (String key : query.getGe().keySet()) {
                    Path<Number> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.ge(objectPath, query.getGe().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }
            //in条件
            if (query.getIn() != null) {
                for (String key : query.getIn().keySet()) {
                    Path<Number> objectPath = root.get(key);
                    CriteriaBuilder.In<Number> in = criteriaBuilder.in(objectPath);
                    List<Number> inWrapper = query.getIn().get(key);
                    for (Number number : inWrapper) {
                        in.value(number);
                    }
                    and = criteriaBuilder.and(and, in);
                }
            }
            //拼装like条件
            if (query.getLike() != null) {
                for (String key : query.getLike().keySet()) {
                    Path<String> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.like(objectPath, query.getLike().get(key));
                    and = criteriaBuilder.and(and, predicate);
                }
            }

            if (query.getOr() != null) {
                for (String key : query.getOr().keySet()) {
                    Path<String> objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.equal(objectPath, query.getOr().get(key));
                    and = criteriaBuilder.or(and, predicate);
                }
            }

            if (query.getBetween() != null) {
                for (String key : query.getBetween().keySet()) {
                    WrapperQuery.Between between = query.getBetween().get(key);
                    Path objectPath = root.get(key);
                    Predicate predicate = criteriaBuilder.between(objectPath, between.getValue1(), between.getValue2());
                    and = criteriaBuilder.or(and, predicate);
                }
            }
            return and;
        };
    }

}
