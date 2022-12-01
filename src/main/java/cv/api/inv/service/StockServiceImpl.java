/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.StockDao;
import cv.api.inv.entity.Stock;
import cv.api.inv.entity.StockKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Transactional
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockDao dao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private ReportService reportService;

    @Override
    public Stock save(Stock stock) throws Exception {
        if (Util1.isNull(stock.getKey().getStockCode())) {
            Integer macId = stock.getMacId();
            String compCode = stock.getKey().getCompCode();
            String stockCode = getStockCode(macId, compCode);
            Integer deptId = stock.getKey().getDeptId();
            Stock valid = findById(new StockKey(stockCode, compCode, deptId));
            if (valid == null) {
                stock.getKey().setStockCode(stockCode);
            } else {
                throw new IllegalStateException("Duplicate Stock Code");
            }
        }
        stock.setIntgUpdStatus(null);
        return dao.save(stock);
    }

    @Override
    public Stock findById(StockKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Stock> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<Stock> findActiveStock(String compCode, Integer deptId) {
        return dao.findActiveStock(compCode, deptId);
    }

    @Override
    public List<String> delete(StockKey key) {
        String stockCode = key.getStockCode();
        String compCode = key.getCompCode();
        List<String> str = reportService.isStockExist(stockCode, compCode);
        if (str.isEmpty()) {
            dao.delete(stockCode);
        }
        return str;
    }

    private String getStockCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "Stock", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public List<Stock> search(String stockCode, String stockType, String cat, String brand, String compCode, Integer deptId) {
        return dao.search(stockCode, stockType, cat, brand, compCode, deptId);
    }


    @Override
    public List<Stock> getStock(String str, String compCode, Integer deptId) {
        return dao.getStock(str, compCode, deptId);
    }

    @Override
    public List<Stock> unUpload() {
        return dao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return  dao.getMaxDate();
    }

}
