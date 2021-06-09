/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.ChargeTypeDao;
import com.cv.inv.api.entity.ChargeType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class ChargeTypeServiceImpl implements ChargeTypeService {

    @Autowired
    private ChargeTypeDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public ChargeType save(ChargeType ct) {
        if (ct.getChargeTypeCode() == null || ct.getChargeTypeCode().isEmpty()) {
            Integer macId = ct.getMacId();
            String compCode = ct.getCompCode();
            ct.setChargeTypeCode(getChargeTypeCode(macId, "ChargeType", "-", compCode));
        }
        return dao.save(ct);
    }

    @Override
    public List<ChargeType> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<ChargeType> search(String ctId, String desp) {
        return dao.search(ctId, desp);
    }

    private String getChargeTypeCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
        return tmpCatCode;
    }
}
