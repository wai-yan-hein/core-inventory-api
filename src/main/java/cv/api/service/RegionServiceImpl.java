/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.RegionDao;
import cv.api.entity.Region;
import cv.api.entity.RegionKey;
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
        if (Util1.isNull(rg.getKey().getRegCode())) {
            Integer macId = rg.getMacId();
            String compCode = rg.getKey().getCompCode();
            String code = getRegionCode(macId, compCode);
            rg.getKey().setRegCode(code);

        }
        return dao.save(rg);
    }

    @Override
    public Region findByCode(RegionKey id) {
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
