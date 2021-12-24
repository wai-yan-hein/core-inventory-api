/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockReceiveDetailHisDao;
import com.cv.inv.api.entity.StockReceiveDetailHis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author lenovo
 */
@Service
@Transactional
public class StockReceiveDetailHisServiceImpl implements StockReceiveDetailHisService {

    @Autowired
    private StockReceiveDetailHisDao dao;

    @Override
    public List<StockReceiveDetailHis> search(String dmgVouId) {
        return dao.search(dmgVouId);
    }

}
