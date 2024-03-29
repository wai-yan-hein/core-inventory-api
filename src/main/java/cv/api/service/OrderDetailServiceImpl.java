/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.OrderHisDetailDao;
import cv.api.entity.OrderDetailKey;
import cv.api.entity.OrderHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderHisDetailDao dao;

    @Override
    public OrderHisDetail save(OrderHisDetail odh) {
        return dao.save(odh);
    }

    @Override
    public List<OrderHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(OrderDetailKey key) {
        return dao.delete(key);
    }
}
