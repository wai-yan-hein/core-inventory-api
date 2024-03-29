/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.StockTypeDao;
import cv.api.entity.StockType;
import cv.api.entity.StockTypeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockTypeServiceImpl implements StockTypeService {

    @Autowired
    private StockTypeDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public StockType save(StockType s) {
        if (Util1.isNull(s.getKey().getStockTypeCode())) {
            String code = getCode(s.getMacId(), s.getKey().getCompCode());
            s.getKey().setStockTypeCode(code);
        }
        return dao.save(s);
    }

    @Override
    public List<StockType> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<StockType> unUpload() {
        return dao.unUpload();
    }


    @Override
    public List<StockType> getStockType(LocalDateTime updatedDate) {
        return dao.getStockType(updatedDate);
    }

    @Override
    public StockType findByCode(StockTypeKey key) {
        return dao.findByCode(key);
    }

    private String getCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "StockType", "-", compCode);
        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }

}
