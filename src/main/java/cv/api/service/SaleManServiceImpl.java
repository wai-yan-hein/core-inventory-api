/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.SaleManDao;
import cv.api.entity.SaleMan;
import cv.api.entity.SaleManKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class SaleManServiceImpl implements SaleManService {

    @Autowired
    private SaleManDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public SaleMan save(SaleMan sm) {
        if (Util1.isNull(sm.getKey().getSaleManCode())) {
            Integer macId = sm.getMacId();
            String compCode = sm.getKey().getCompCode();
            String code = getSaleManCode(macId, compCode);
            sm.getKey().setSaleManCode(code);
        }
        return dao.save(sm);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public SaleMan findByCode(SaleManKey key) {
        return dao.findByCode(key);
    }

    @Override
    public List<SaleMan> unUpload() {
        return dao.unUpload();
    }

    @Override
    public List<SaleMan> getSaleMan(LocalDateTime updatedDate) {
        return dao.getSaleMan(updatedDate);
    }
    private String getSaleManCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "SM", "-", compCode);
        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }

    @Override
    public List<SaleMan> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }
}
