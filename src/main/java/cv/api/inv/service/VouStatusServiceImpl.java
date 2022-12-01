/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.VouStatusDao;
import cv.api.inv.entity.VouStatus;
import cv.api.inv.entity.VouStatusKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
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
        if (Objects.isNull(vs.getKey().getCode())) {
            Integer macId = vs.getMacId();
            String compCode = vs.getKey().getCompCode();
            vs.getKey().setCode(getVouStatusCode(macId, compCode));
        }
        vs.setIntgUpdStatus(null);
        return vouDao.save(vs);
    }

    @Override
    public List<VouStatus> findAll(String compCode, Integer deptId) {
        return vouDao.findAll(compCode, deptId);
    }

    @Override
    public int delete(String id) {
        return vouDao.delete(id);
    }

    @Override
    public VouStatus findById(VouStatusKey id) {
        return vouDao.findById(id);
    }

    @Override
    public List<VouStatus> search(String description) {
        return vouDao.search(description);
    }

    @Override
    public List<VouStatus> unUpload() {
        return vouDao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return vouDao.getMaxDate();
    }

    @Override
    public List<VouStatus> getVouStatus(String updatedDate) {
        return vouDao.getVouStatus(updatedDate);
    }

    private String getVouStatusCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "VouStatus", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
