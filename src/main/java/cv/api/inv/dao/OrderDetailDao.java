/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.OrderDetail;

import java.util.List;

/**
 * @author wai yan
 */
 public interface OrderDetailDao {

     OrderDetail save(OrderDetail order);

     List<OrderDetail> findActiveOrder();

     int delete(String id);

     List<OrderDetail> search(String orderCode);

}
