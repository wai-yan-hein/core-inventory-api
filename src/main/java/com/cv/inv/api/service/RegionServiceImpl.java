/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.RegionDao;
import com.cv.inv.api.entity.Region;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
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
    public Region save(Region rg) {
        if (rg.getRegCode() == null || rg.getRegCode().isEmpty()) {
            Integer macId = rg.getMacId();
            String compCode = rg.getCompCode();
            rg.setRegCode(getRegiionCode(macId, "Region", "-", compCode));
        }
        return dao.save(rg);
    }

    @Override
    public Region findById(String id) {
        Region region = dao.findById(id);
        return region;
    }

    @Override
    public List<Region> search(String code, String name, String compCode, String parentCode) {
        List<Region> listRegion = dao.search(code, name, compCode, parentCode);
        return listRegion;
    }

    @Override
    public int delete(String code, String compCode) {
        int cnt = dao.delete(code, compCode);
        return cnt;
    }

    private String getRegiionCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
        return tmpCatCode;
    }
}
