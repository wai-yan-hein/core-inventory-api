/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.TraderDao;
import cv.api.entity.Trader;
import cv.api.entity.TraderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
@Service
@Transactional
@Slf4j
public class TraderServiceImpl implements TraderService {

    @Autowired
    private TraderDao dao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private ReportService reportService;


    @Override
    public Trader findById(TraderKey key) {
        return dao.findById(key);
    }

    @Override
    public Trader findByRFID(String rfId, String compCode, Integer deptId) {
        return dao.findByRFID(rfId, compCode, deptId);
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode, Integer deptId) {
        return dao.searchTrader(str, type, compCode, deptId);
    }

    @Override
    public Trader saveTrader(Trader td) {
        if (Util1.isNull(td.getKey().getCode())) {
            String compCode = td.getKey().getCompCode();
            String type = td.getType();
            String code = getTraderCode(type, compCode);
            Trader valid = findById(td.getKey());
            if (valid == null) {
                td.getKey().setCode(code);
            } else {
                throw new IllegalStateException("Duplicate Trader Code");
            }
        }
        return dao.saveTrader(td);
    }

    private String getTraderCode(String option, String compCode) {
        int seqNo = seqService.getSequence(0, option, "-", compCode);
        return option.toUpperCase() + String.format("%0" + 6 + "d", seqNo);
    }

    @Override
    public List<Trader> search(String regionCode, String coaCode) {
        return dao.search(regionCode, coaCode);
    }

    @Override
    public List<Trader> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public List<Trader> findAll() {
        return dao.findAll();
    }

    @Override
    public List<String> delete(TraderKey key) {
        List<String> str = reportService.isTraderExist(key.getCode(), key.getCompCode());
        if (str.isEmpty()) {
            dao.delete(key);
        }
        return str;
    }

    @Override
    public List<Trader> unUploadTrader() {
        return dao.unUploadTrader();
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Trader> getTrader(String updatedDate) {
        return dao.getTrader(updatedDate);
    }

    @Override
    public List<Trader> findCustomer(String compCode, Integer deptId) {
        return dao.findCustomer(compCode, deptId);
    }

    @Override
    public List<Trader> findSupplier(String compCode, Integer deptId) {
        return dao.findSupplier(compCode, deptId);
    }
}
