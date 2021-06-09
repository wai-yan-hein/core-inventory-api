/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.VouStatusDao;
import com.cv.inv.api.entity.VouStatus;
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
public class VouStatusServiceImpl implements VouStatusService {

    @Autowired
    private VouStatusDao vouDao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public VouStatus save(VouStatus vs) {
        if (vs.getVouStatusCode() == null || vs.getVouStatusCode().isEmpty()) {
            Integer macId = vs.getMacId();
            String compCode = vs.getCompCode();
            vs.setVouStatusCode(getVouStatusCode(macId, "VouStatus", "-", compCode));
        }
        return vouDao.save(vs);
    }

    @Override
    public List<VouStatus> findAll() {
        return vouDao.findAll();
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
    public List<VouStatus> search(String statusDesp) {
        return vouDao.search(statusDesp);
    }

    private String getVouStatusCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
        return tmpCatCode;
    }
}
