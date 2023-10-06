/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.StockCriteriaDao;
import cv.api.dao.StockDao;
import cv.api.entity.Stock;
import cv.api.entity.StockCriteria;
import cv.api.entity.StockCriteriaKey;
import cv.api.entity.StockKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Transactional
@Service
@RequiredArgsConstructor
public class StockCriteriaServiceImpl implements StockCriteriaService {

    private final StockCriteriaDao dao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private ReportService reportService;

    @Override
    public StockCriteria save(StockCriteria stock) {
        if (Util1.isNull(stock.getKey().getCriteriaCode())) {
//            Integer macId = stock.getMacId();
            String compCode = stock.getKey().getCompCode();
            String code = getCode(compCode);
            StockCriteria valid = findById(new StockCriteriaKey(code, compCode));
            if (valid == null) {
                stock.getKey().setCriteriaCode(code);
            } else {
                throw new IllegalStateException("Duplicate Stock Criteria Code");
            }
        }
        return dao.save(stock);
    }

    @Override
    public StockCriteria findById(StockCriteriaKey key) {
        return dao.findById(key);
    }

    @Override
    public List<StockCriteria> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public List<StockCriteria> findActiveStock(String compCode) {
        return dao.findActiveStock(compCode);
    }

    @Override
    public List<General> delete(StockCriteriaKey key) {
        String code = key.getCriteriaCode();
        String compCode = key.getCompCode();
        List<General> str = reportService.isStockExist(code, compCode);
        if (str.isEmpty()) {
            dao.delete(key);
        }
        return str;
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockCriteria", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }

    @Override
    public List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite) {
        return dao.search(stockCode, stockType, cat, brand, compCode, orderFavorite);
    }


    @Override
    public List<StockCriteria> getStockCriteria(String str, String compCode) {
        return dao.getStock(str, compCode);
    }

    @Override
    public List<StockCriteria> getService(String compCode) {
        return dao.getService(compCode);
    }

    @Override
    public List<StockCriteria> unUpload() {
        return dao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<StockCriteria> getStock(LocalDateTime updatedDate) {
        return dao.getStock(updatedDate);
    }

    @Override
    public StockCriteria updateStock(StockCriteria stock) {
        return dao.updateStock(stock);
    }
}
