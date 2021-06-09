/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Order;
import com.cv.inv.api.entity.OrderDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface OrderService {

    public Order save(Order order, List<OrderDetail> od);

    public List<Order> findActiveOrder(String fromDate, String toDate, String cusId, String orderCode);

    public int delete(String id);
}
