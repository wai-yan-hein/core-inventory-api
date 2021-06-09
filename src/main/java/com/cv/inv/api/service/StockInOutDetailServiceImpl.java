/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockInOutDetailDao;
import com.cv.inv.api.entity.StockInOutDetail;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class StockInOutDetailServiceImpl implements StockInOutDetailService {

    @Autowired
    private StockInOutDetailDao dao;

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        return dao.save(stock);
    }

    @Override
    public List<StockInOutDetail> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark) {
        return dao.search(fromDate, toDate, stockCode, locId, option, remark);
    }

    @Override
    public int delete(Integer id) {
        return dao.delete(id);
    }

    @Override
    public List<StockInOutDetail> search(String batchCode) {
        return dao.search(batchCode);
    }

}
