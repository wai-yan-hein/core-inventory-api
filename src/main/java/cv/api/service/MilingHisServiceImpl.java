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
public class MilingHisServiceImpl implements MilingHisService {

    @Autowired
    private MilingHisDao hDao;
    @Autowired
    private MilingRawDao rDao;
    @Autowired
    private MilingOutDao oDao;
    @Autowired
    private MilingExpenseDao eDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public MilingHis save(MilingHis milingHis) {
        milingHis.setVouDate(Util1.toDateTime(milingHis.getVouDate()));
        if (Util1.isNullOrEmpty(milingHis.getKey().getVouNo())) {
            milingHis.getKey().setVouNo(getVoucherNo(milingHis.getKey().getDeptId(), milingHis.getMacId(), milingHis.getKey().getCompCode()));
        }
        List<MilingRawDetail> listRaw = milingHis.getListRaw();
        List<MilingRawDetailKey> listRawDel = milingHis.getListRawDel();
        List<MilingExpense> listExp = milingHis.getListExpense();
        List<MilingExpenseKey> listExpDel = milingHis.getListExpenseDel();
        List<MilingOutDetail> listOut = milingHis.getListOutput();
        List<MilingOutDetailKey> listOutDel = milingHis.getListOutputDel();
        String vouNo = milingHis.getKey().getVouNo();
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
            MilingRawDetail cSd = listRaw.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                MilingRawDetailKey key = new MilingRawDetailKey();
                key.setDeptId(milingHis.getKey().getDeptId());
                key.setCompCode(milingHis.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MilingRawDetail pSd = listRaw.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                rDao.save(cSd);
            }
        }
        for (int i = 0; i < listOut.size(); i++) {
            MilingOutDetail cSd = listOut.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                MilingOutDetailKey key = new MilingOutDetailKey();
                key.setDeptId(milingHis.getKey().getDeptId());
                key.setCompCode(milingHis.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MilingOutDetail pSd = listOut.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                oDao.save(cSd);
            }
        }
        for (int i = 0; i < listExp.size(); i++) {
            MilingExpense cSd = listExp.get(i);
            if (cSd.getKey() != null && cSd.getKey().getExpenseCode() != null) {
                    MilingExpenseKey key = new MilingExpenseKey();
                    key.setExpenseCode(cSd.getKey().getExpenseCode());
                    key.setCompCode(milingHis.getKey().getCompCode());
                    key.setVouNo(vouNo);
                    key.setUniqueId(null);
                    cSd.setKey(key);

            }
            if (cSd.getKey() != null && cSd.getKey().getExpenseCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        MilingExpense pSd = listExp.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                eDao.save(cSd);
            }
        }
        hDao.save(milingHis);
        milingHis.setListRaw(listRaw);
        milingHis.setListOutput(listOut);
        milingHis.setListExpense(listExp);
        return milingHis;
    }

    @Override
    public MilingHis update(MilingHis milingHis) {
        return hDao.save(milingHis);
    }

    @Override
    public List<MilingHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return hDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public MilingHis findById(MilingHisKey id) {
        return hDao.findById(id);
    }

    @Override
    public void delete(MilingHisKey key) throws Exception {
        hDao.delete(key);
    }

    @Override
    public void restore(MilingHisKey key) throws Exception {
        hDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }


    @Override
    public List<MilingHis> unUploadVoucher(LocalDateTime syncDate) {
        return hDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<MilingHis> unUpload(String syncDate) {
        return hDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return hDao.getMaxDate();
    }


    @Override
    public void truncate(MilingHisKey key) {
        hDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return hDao.getVoucherInfo(vouDate, compCode, depId);
    }


}
