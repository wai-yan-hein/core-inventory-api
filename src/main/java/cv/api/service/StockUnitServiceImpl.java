/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.StockUnitDao;
import cv.api.entity.StockUnit;
import cv.api.entity.StockUnitKey;
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
public class StockUnitServiceImpl implements StockUnitService {

    @Autowired
    private StockUnitDao dao;

    @Override
    public StockUnit save(StockUnit unit) {
        return dao.save(unit);
    }

    @Override
    public List<StockUnit> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<StockUnit> unUpload() {
        return dao.unUpload();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<StockUnit> getUnit(LocalDateTime updatedDate) {
        return dao.getUnit(updatedDate);
    }

    @Override
    public StockUnit findByCode(StockUnitKey code) {
        return dao.findByCode(code);
    }

}
