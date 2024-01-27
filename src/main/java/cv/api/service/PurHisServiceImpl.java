/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.*;
import cv.api.entity.*;
import cv.api.model.VDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PurHisServiceImpl implements PurHisService {

    private final PurHisDao phDao;
    private final PurHisDetailService pdDao;
    private final SeqTableDao seqDao;
    private final PurExpenseDao purExpenseDao;
    private final WeightDao weightDao;
    private final LandingHisDao landingDao;

    @Override
    public PurHis save(PurHis ph) {
        ph.setVouDate(Util1.toDateTime(ph.getVouDate()));
        if (Util1.isNullOrEmpty(ph.getKey().getVouNo())) {
            ph.getKey().setVouNo(getVoucherNo(ph.getDeptId(), ph.getMacId(), ph.getKey().getCompCode()));
        }
        List<PurHisDetail> listSD = ph.getListPD();
        List<PurDetailKey> listDel = ph.getListDel();
        String vouNo = ph.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(pdDao::delete);
        }
        List<PurExpense> listExp = ph.getListExpense();
        if (listExp != null) {
            for (int i = 0; i < listExp.size(); i++) {
                PurExpense e = listExp.get(i);
                if (Util1.getFloat(e.getAmount()) > 0) {
                    if (e.getKey().getExpenseCode() != null) {
                        if (e.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                e.getKey().setUniqueId(1);
                            } else {
                                PurExpense pe = listExp.get(i - 1);
                                e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                            }
                        }
                        e.getKey().setVouNo(vouNo);
                        e.setPercent(Util1.getDouble(e.getPercent()));
                        purExpenseDao.save(e);
                    }
                }
            }
        }
        for (int i = 0; i < listSD.size(); i++) {
            PurHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                PurDetailKey key = new PurDetailKey();
                key.setCompCode(ph.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(ph.getDeptId());
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        PurHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                pdDao.save(cSd);
            }
            phDao.save(ph);
            ph.setListPD(listSD);
        }
        updateWeight(ph, true);
        updateLanding(ph, true);
        return ph;
    }

    private void updateWeight(PurHis ph, boolean status) {
        String weightVouNo = ph.getWeightVouNo();
        if (!Util1.isNullOrEmpty(weightVouNo)) {
            WeightHisKey key = new WeightHisKey();
            key.setVouNo(weightVouNo);
            key.setCompCode(ph.getKey().getCompCode());
            WeightHis wh = weightDao.findById(key);
            if (wh != null) {
                wh.setPost(status);
                weightDao.save(wh);
            }
        }
    }

    private void updateLanding(PurHis ph, boolean status) {
        String landVouNo = ph.getLandVouNo();
        if (!Util1.isNullOrEmpty(landVouNo)) {
            LandingHisKey key = new LandingHisKey();
            key.setVouNo(landVouNo);
            key.setCompCode(ph.getKey().getCompCode());
            LandingHis wh = landingDao.findByCode(key);
            if (wh != null) {
                wh.setPost(status);
                landingDao.save(wh);
            }
        }
    }

    @Override
    public PurHis update(PurHis ph) {
        return phDao.save(ph);
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return phDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public PurHis findById(PurHisKey id) {
        return phDao.findById(id);
    }

    @Override
    public void delete(PurHisKey key) throws Exception {
        PurHis ph = phDao.findById(key);
        if (ph != null) {
            updateWeight(ph, false);
            updateLanding(ph, false);
        }
        phDao.delete(key);
    }

    @Override
    public void restore(PurHisKey key) throws Exception {
        phDao.restore(key);
    }

    @Override
    public List<PurHis> unUploadVoucher(LocalDateTime syncDate) {
        return phDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<PurHis> unUpload(String syncDate) {
        return phDao.unUpload(syncDate);
    }


    @Override
    public List<PurHis> search(String updatedDate, List<String> keys) {
        return phDao.search(updatedDate, keys);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "PURCHASE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode, String tranType) {
        return phDao.getDescription(str, compCode, tranType);
    }
}
