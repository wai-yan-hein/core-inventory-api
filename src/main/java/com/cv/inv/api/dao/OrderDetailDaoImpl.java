/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OrderDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class OrderDetailDaoImpl extends AbstractDao<String, OrderDetail> implements OrderDetailDao {

    @Override
    public OrderDetail save(OrderDetail od) {
        persist(od);
        return od;
    }

    @Override
    public List<OrderDetail> findActiveOrder() {
        return null;
    }

    @Override
    public int delete(String id) {
        return 0;
    }

    @Override
    public List<OrderDetail> search(String orderCode) {
        String hsql = "select o from OrderDetail o where o.orderCode = '" + orderCode + "'";
        return findHSQL(hsql);
    }

}
