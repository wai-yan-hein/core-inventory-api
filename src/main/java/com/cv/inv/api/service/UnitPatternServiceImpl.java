/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.UnitPatternDao;
import com.cv.inv.api.entity.UnitPattern;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class UnitPatternServiceImpl implements UnitPatternService {

    @Autowired
    private UnitPatternDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public UnitPattern save(UnitPattern up) {
        if (up.getPatternCode() == null || up.getPatternCode().isEmpty()) {
            Integer macId = up.getMacId();
            String compCode = up.getCompCode();
            up.setPatternCode(getUnitPatternCode(macId, "UnitPattern", "-", compCode));
        }
        return dao.save(up);
    }

    @Override
    public List<UnitPattern> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    private String getUnitPatternCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }
}
