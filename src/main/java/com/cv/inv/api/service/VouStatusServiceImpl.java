/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.VouStatusDao;
import com.cv.inv.api.entity.VouStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class VouStatusServiceImpl implements VouStatusService {

    @Autowired
    private VouStatusDao vouDao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public VouStatus save(VouStatus vs) {
        if (Objects.isNull(vs.getCode())) {
            Integer macId = vs.getMacId();
            String compCode = vs.getCompCode();
            vs.setCode(getVouStatusCode(macId, compCode));
        }
        return vouDao.save(vs);
    }

    @Override
    public List<VouStatus> findAll(String compCode) {
        return vouDao.findAll(compCode);
    }

    @Override
    public int delete(String id) {
        return vouDao.delete(id);
    }

    @Override
    public VouStatus findById(String id) {
        return vouDao.findById(id);
    }

    @Override
    public List<VouStatus> search(String description) {
        return vouDao.search(description);
    }

    private String getVouStatusCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "VouStatus", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
