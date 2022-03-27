/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Order;

import java.util.List;

/**
 * @author wai yan
 */
 public interface OrderDao {

     Order save(Order order);

     List<Order> findActiveOrder(String fromDate, String toDate, String cusId, String orderCode);

     int delete(String id);
}
