/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockBrandDao;
import com.cv.inv.api.entity.StockBrand;
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
public class StockBrandServiceImpl implements StockBrandService {

    @Autowired
    private StockBrandDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public StockBrand save(StockBrand sb) throws Exception{
        if (sb.getBrandCode() == null || sb.getBrandCode().isEmpty()) {
            Integer macId = sb.getMacId();
            String compCode = sb.getCompCode();
            String code = getStockBrandCode(macId, "StockBrand", "-", compCode);
            StockBrand valid = findByCode(code);
            if (valid == null) {
                sb.setBrandCode(code);
            } else {
                throw new IllegalStateException("Duplicate Brand Code");
            }
        }
        return dao.save(sb);
    }

    @Override
    public List<StockBrand> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    private String getStockBrandCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }

    @Override
    public StockBrand findByCode(String code) {
        return dao.findByCode(code);
    }
}
