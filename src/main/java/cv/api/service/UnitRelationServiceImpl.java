/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.UnitRelationDao;
import cv.api.dao.UnitRelationDetailDao;
import cv.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
 */
@Service
@Transactional
public class UnitRelationServiceImpl implements UnitRelationService {
    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private UnitRelationDao dao;
    @Autowired
    private UnitRelationDetailDao detailDao;

    @Override
    public UnitRelation save(UnitRelation ur) {
        if (Objects.isNull(ur.getKey().getRelCode())) {
            ur.getKey().setRelCode(getUnitCode());
        }
        List<UnitRelationDetail> detail = ur.getDetailList();
        detail.removeIf(rd -> rd.getUnit() == null);
        int size = detail.size();
        //cal smallest
        if (size > 0) {
            while (size != 0) {
                int lastIndex = size - 1;
                // last index
                if (lastIndex == detail.size() - 1) {
                    UnitRelationDetail ud = detail.get(lastIndex);
                    UnitRelationDetailKey key = new UnitRelationDetailKey(lastIndex + 1, ur.getKey().getRelCode(), ur.getKey().getCompCode());
                    ud.setKey(key);
                    ud.setDeptId(ur.getDeptId());
                    ud.setSmallestQty(1.0f);
                } else {
                    UnitRelationDetail rd = detail.get(size - 1);
                    UnitRelationDetailKey key = new UnitRelationDetailKey(lastIndex + 1, ur.getKey().getRelCode(), ur.getKey().getCompCode());
                    rd.setKey(key);
                    rd.setDeptId(ur.getDeptId());
                    float qty = detail.get(size).getQty();
                    float small = detail.get(size).getSmallestQty();
                    rd.setSmallestQty(qty * small);
                }
                size--;
            }
        }
        detail.forEach(ud -> detailDao.save(ud));
        ur.setRelName(getRelStr(detail));
        return dao.save(ur);
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return dao.findByKey(key);
    }

    private String getRelStr(List<UnitRelationDetail> listRD) {
        StringBuilder relStr = new StringBuilder();
        for (UnitRelationDetail ud : listRD) {
            relStr.append(String.format("%s%s%s", formatter.format(ud.getQty()), ud.getUnit(), "*"));
        }
        String str = relStr.toString();
        return str.substring(0, str.length() - 1);
    }

    @Override
    public List<UnitRelation> findRelation(String compCode, Integer deptId) {
        return dao.findRelation(compCode, deptId);
    }

    @Override
    public List<StockUnit> getRelation(String relCode, String compCode, Integer deptId) {
        return null;
    }

    @Override
    public UnitRelationDetail save(UnitRelationDetail unit) {
        return detailDao.save(unit);
    }

    @Override
    public List<UnitRelationDetail> getRelationDetail(String code, String compCode) {
        return detailDao.getRelationDetail(code, compCode);
    }

    @Override
    public UnitRelationDetail findByKey(UnitRelationDetailKey key) {
        return detailDao.findByKey(key);
    }

    @Override
    public List<UnitRelation> unUpload() {
        return dao.unUpload();
    }


    @Override
    public List<UnitRelation> getRelation(LocalDateTime updatedDate) {
        return dao.getRelation(updatedDate);
    }

    private String getUnitCode() {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(0, "Unit", period, "-");
        return period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
