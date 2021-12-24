/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.StockTypeDao;
import com.cv.inv.api.entity.StockType;
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
public class StockTypeServiceImpl implements StockTypeService {

    @Autowired
    private StockTypeDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public StockType save(StockType s) throws Exception {
        if (Util1.isNull(s.getStockTypeCode())) {
            String code = getCode(s.getMacId(), "StockType", "-", s.getCompCode());
            StockType valid = findByCode(code);
            if (valid == null) {
                s.setStockTypeCode(code);
            } else {
                throw new IllegalStateException("Duplicate Stock Type");
            }
        }
        return dao.save(s);
    }

    @Override
    public List<StockType> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public StockType findByCode(String code) {
        return dao.findByCode(code);
    }

    private String getCode(Integer macId, String option, String period, String compCode) {
        int seqNo = seqService.getSequence(macId, option, period, compCode);
        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }

}
