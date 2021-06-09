/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OrderDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface OrderDetailDao {

    public OrderDetail save(OrderDetail order);

    public List<OrderDetail> findActiveOrder();

    public int delete(String id);

    public List<OrderDetail> search(String orderCode);

}
