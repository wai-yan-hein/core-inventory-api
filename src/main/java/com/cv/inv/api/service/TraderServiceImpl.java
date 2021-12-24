/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.TraderDao;
import com.cv.inv.api.entity.Trader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class TraderServiceImpl implements TraderService {

    @Autowired
    private TraderDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Trader findByCode(String id) {
        return dao.findByCode(id);
    }

    @Override
    public List<Trader> searchTrader(String code, String name, String address,
            String phone, String parentCode, String compCode, String appTraderCode) {
        return dao.searchTrader(code, name, address, phone,
                parentCode, compCode, appTraderCode);
    }

    @Override
    public Trader saveTrader(Trader td) throws Exception {
        if (Util1.isNull(td.getCode())) {
            Integer macId = td.getMacId();
            String compCode = td.getCompCode();
            String type = td.getType();
            String code = getTraderCode(macId, type, "-", compCode);
            Trader valid = findByCode(code);
            if (valid == null) {
                td.setCode(code);
            } else {
                throw new IllegalStateException("Duplicate Trader Code");
            }
        }
        return dao.saveTrader(td);
    }

    @Override
    public List<Trader> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }

    private String getTraderCode(Integer macId, String option, String period, String compCode) {
        int seqNo = seqService.getSequence(macId, option, period, compCode);
        return option.toUpperCase() + String.format("%0" + 5 + "d", seqNo) + "-" + String.format("%0" + 3 + "d", macId);
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
    public int delete(String code) {
        return dao.delete(code);
    }

    @Override
    public List<Trader> findCustomer(String compCode) {
        return dao.findCustomer(compCode);
    }

    @Override
    public List<Trader> findSupplier(String compCode) {
        return dao.findSupplier(compCode);
    }
}
