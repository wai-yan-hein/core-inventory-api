/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.OrderDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface OrderDetailService {

    public OrderDetail save(OrderDetail order);

    public List<OrderDetail> findActiveOrder();

    public List<OrderDetail> search(String orderCode);

    public int delete(String id);
}
