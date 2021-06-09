/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockInOutDao;
import com.cv.inv.api.entity.StockInOut;
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
public class StockInOutServiceImpl implements StockInOutService {

    @Autowired
    private StockInOutDao dao;
    @Autowired
    private StockInOutDetailService detailService;

    @Override
    public StockInOut findById(String id) {
        return dao.findById(id);
    }

    @Override
    public StockInOut save(StockInOut stock) {
        return dao.save(stock);
    }

    @Override
    public List<StockInOut> search(String batchCode, String fromDate, String toDate, String desp, String remark) {
        return dao.search(batchCode, fromDate, toDate, desp, remark);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public StockInOut save(StockInOut stock, List<StockInOutDetail> listDetail) {
        for (int i = 0; i < listDetail.size(); i++) {
            StockInOutDetail cRD = listDetail.get(i);
            if (cRD.getUniqueId() == null) {
                if (i == 0) {
                    cRD.setUniqueId(1);
                } else {
                    StockInOutDetail pRD = listDetail.get(i - 1);
                    cRD.setUniqueId(pRD.getUniqueId() + 1);
                }
            }
        }
        String vouNo = stock.getBatchCode();
        for (StockInOutDetail rd : listDetail) {
            if (rd.getStock().getStockCode() != null) {
                if (rd.getCode() == null || rd.getCode().isEmpty()) {
                    String code = vouNo + "-" + rd.getUniqueId();
                    rd.setCode(code);
                    rd.setBatchCode(vouNo);
                }
            }
            detailService.save(rd);
        }
        return dao.save(stock);
    }

}
