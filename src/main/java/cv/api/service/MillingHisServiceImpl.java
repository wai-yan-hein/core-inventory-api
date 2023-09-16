/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.*;
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
public class MillingHisServiceImpl implements MillingHisService {

    @Autowired
    private MillingHisDao hDao;
    @Autowired
    private MillingRawDao rDao;
    @Autowired
    private MillingOutDao oDao;
    @Autowired
    private MillingExpenseDao eDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public MillingHis save(MillingHis milling) {
        milling.setVouDate(Util1.toDateTime(milling.getVouDate()));
        Integer deptId = milling.getDeptId();
        if (Util1.isNullOrEmpty(milling.getKey().getVouNo())) {
            milling.getKey().setVouNo(getVoucherNo(deptId, milling.getMacId(), milling.getKey().getCompCode()));
        }
        List<MillingRawDetail> listRaw = milling.getListRaw();
        List<MillingRawDetailKey> listRawDel = milling.getListRawDel();
        List<MillingExpense> listExp = milling.getListExpense();
        List<MillingExpenseKey> listExpDel = milling.getListExpenseDel();
        List<MillingOutDetail> listOut = milling.getListOutput();
        List<MillingOutDetailKey> listOutDel = milling.getListOutputDel();
        String vouNo = milling.getKey().getVouNo();
        //backup
        if (listRawDel != null) {
            listRawDel.forEach(key -> rDao.delete(key));
        }
        if (listOutDel != null) {
            listOutDel.forEach(key -> oDao.delete(key));
        }
        if (listExpDel != null) {
            listExpDel.forEach(key -> eDao.delete(key));
        }
        for (int i = 0; i < listRaw.size(); i++) {
            MillingRawDetail cSd = listRaw.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                MillingRawDetailKey key = new MillingRawDetailKey();
                key.setCompCode(milling.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MillingRawDetail pSd = listRaw.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.setDeptId(deptId);
                rDao.save(cSd);
            }
        }
        for (int i = 0; i < listOut.size(); i++) {
            MillingOutDetail cSd = listOut.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                MillingOutDetailKey key = new MillingOutDetailKey();
                key.setCompCode(milling.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MillingOutDetail pSd = listOut.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.setDeptId(deptId);
                oDao.save(cSd);
            }
        }
        for (int i = 0; i < listExp.size(); i++) {
            MillingExpense cSd = listExp.get(i);
            if (cSd.getKey() != null && cSd.getKey().getExpenseCode() != null) {
                    MillingExpenseKey key = new MillingExpenseKey();
                    key.setExpenseCode(cSd.getKey().getExpenseCode());
                    key.setCompCode(milling.getKey().getCompCode());
                    key.setVouNo(vouNo);
                    key.setUniqueId(0);
                    cSd.setKey(key);

            }
            if (cSd.getKey() != null && cSd.getKey().getExpenseCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MillingExpense pSd = listExp.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                eDao.save(cSd);
            }
        }
        hDao.save(milling);
        milling.setListRaw(listRaw);
        milling.setListOutput(listOut);
        milling.setListExpense(listExp);
        return milling;
    }

    @Override
    public MillingHis update(MillingHis milling) {
        return hDao.save(milling);
    }

    @Override
    public List<MillingHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return hDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public MillingHis findById(MillingHisKey id) {
        return hDao.findById(id);
    }

    @Override
    public void delete(MillingHisKey key) throws Exception {
        hDao.delete(key);
    }

    @Override
    public void restore(MillingHisKey key) throws Exception {
        hDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }


    @Override
    public List<MillingHis> unUploadVoucher(LocalDateTime syncDate) {
        return hDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<MillingHis> unUpload(String syncDate) {
        return hDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return hDao.getMaxDate();
    }


    @Override
    public void truncate(MillingHisKey key) {
        hDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return hDao.getVoucherInfo(vouDate, compCode, depId);
    }


}
