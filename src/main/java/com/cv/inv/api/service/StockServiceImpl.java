/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.DuplicateException;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.StockDao;
import com.cv.inv.api.entity.Stock;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Transactional
@Service
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);
    @Autowired
    private StockDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Stock save(Stock stock) throws Exception {
        if (Util1.isNull(stock.getStockCode())) {
            Integer macId = stock.getMacId();
            String compCode = stock.getCompCode();
            String stockCode = getStockCode(macId, "Stock", "-", compCode);
            Stock valid = findById(stockCode);
            if (valid == null) {
                stock.setStockCode(stockCode);
            } else {
                throw new DuplicateException("Duplicate Stock Code");
            }
        }
        return dao.save(stock);
    }

    @Override
    public Stock findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public List<Stock> findActiveStock() {
        return dao.findActiveStock();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    private String getStockCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);
        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
        return tmpCatCode;
    }

    @Override
    public List<Stock> search(String stockType) {
        return dao.search(stockType);
    }

    @Override
    public List<Stock> searchC(String stockCat) {
        return dao.searchC(stockCat);
    }

    @Override
    public List<Stock> searchB(String stockBrand) {
        return dao.searchB(stockBrand);
    }

    @Override
    public List<Stock> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }
}
