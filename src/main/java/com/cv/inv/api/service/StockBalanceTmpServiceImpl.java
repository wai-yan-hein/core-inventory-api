/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockBalanceTmpDao;
import com.cv.inv.api.entity.StockBalanceTmp;
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
public class StockBalanceTmpServiceImpl implements StockBalanceTmpService {

    @Autowired
    private StockBalanceTmpDao dao;

    @Override
    public StockBalanceTmp save(StockBalanceTmp balance) {
        return dao.save(balance);
    }

    @Override
    public List<StockBalanceTmp> search(String machineId) {
        return dao.search(machineId);
    }

}
