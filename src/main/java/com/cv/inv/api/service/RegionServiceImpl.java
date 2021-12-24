/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.RegionDao;
import com.cv.inv.api.entity.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author WSwe
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public Region save(Region rg) throws Exception {
        if (Util1.isNull(rg.getRegCode())) {
            Integer macId = rg.getMacId();
            String compCode = rg.getCompCode();
            String code = getRegionCode(macId, compCode);
            Region valid = findByCode(code);
            if (valid == null) {
                rg.setRegCode(code);
            } else {
                throw new IllegalStateException("Duplicate Region Code");
            }
        }
        return dao.save(rg);
    }

    @Override
    public Region findByCode(String id) {
        return dao.findByCode(id);
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        return dao.search(code, name, compCode, parentCode);
    }

    @Override
    public int delete(String code) {
        return dao.delete(code);
    }

    private String getRegionCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "Region", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    @Override
    public List<Region> findAll(String compCode) {
        return dao.findAll(compCode);
    }
}
