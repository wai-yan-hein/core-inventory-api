/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.OrderDetail;

import java.util.List;

/**
 * @author wai yan
 */
 public interface OrderDetailService {

     OrderDetail save(OrderDetail order);

     List<OrderDetail> findActiveOrder();

     List<OrderDetail> search(String orderCode);

     int delete(String id);
}
