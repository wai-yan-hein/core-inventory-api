/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.OrderDetailDao;
import com.cv.inv.api.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailDao dao;

    @Override
    public OrderDetail save(OrderDetail order) {
        return dao.save(order);
    }

    @Override
    public List<OrderDetail> findActiveOrder() {
        return dao.findActiveOrder();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<OrderDetail> search(String orderCode) {
        return dao.search(orderCode);
    }

}
