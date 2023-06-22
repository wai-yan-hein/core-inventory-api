/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.PurExpenseDao;
import cv.api.dao.PurHisDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PurHisServiceImpl implements PurHisService {

    @Autowired
    private PurHisDao phDao;
    @Autowired
    private PurHisDetailService pdDao;
    @Autowired
    private SeqTableDao seqDao;
    @Autowired
    private PurExpenseDao purExpenseDao;


    @Override
    public PurHis save(PurHis ph) {
        ph.setVouDate(Util1.toDateTime(ph.getVouDate()));
        if (Util1.isNullOrEmpty(ph.getKey().getVouNo())) {
            ph.getKey().setVouNo(getVoucherNo(ph.getKey().getDeptId(), ph.getMacId(), ph.getKey().getCompCode()));
        }
        List<PurHisDetail> listSD = ph.getListPD();
        List<PurDetailKey> listDel = ph.getListDel();
        String vouNo = ph.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> pdDao.delete(key));
        }
        List<PurExpense> listExp = ph.getListExpense();
        if (listExp != null) {
            for (int i = 0; i < listExp.size(); i++) {
                PurExpense e = listExp.get(i);
                if (Util1.getFloat(e.getAmount()) > 0) {
                    if (e.getKey().getExpenseCode() != null) {
                        if (e.getKey().getUniqueId() == null) {
                            if (i == 0) {
                                e.getKey().setUniqueId(1);
                            } else {
                                PurExpense pe = listExp.get(i - 1);
                                e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                            }
                        }
                        e.getKey().setVouNo(vouNo);
                        purExpenseDao.save(e);
                    }
                }
            }
        }
        for (int i = 0; i < listSD.size(); i++) {
            PurHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                PurDetailKey key = new PurDetailKey();
                key.setDeptId(ph.getKey().getDeptId());
                key.setCompCode(ph.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
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
        return ph;
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
    public Date getMaxDate() {
        return phDao.getMaxDate();
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

}
