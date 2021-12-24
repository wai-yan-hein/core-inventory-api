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

     OrderDetail save(OrderDetail order);

     List<OrderDetail> findActiveOrder();

     List<OrderDetail> search(String orderCode);

     int delete(String id);
}
