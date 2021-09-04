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

     OrderDetail save(OrderDetail order);

     List<OrderDetail> findActiveOrder();

     int delete(String id);

     List<OrderDetail> search(String orderCode);

}
