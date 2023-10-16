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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
@RequiredArgsConstructor
public class SaleHisServiceImpl implements SaleHisService {

    private final SaleHisDao shDao;
    private final SaleHisDetailDao sdDao;
    private final SeqTableDao seqDao;
    private final SaleExpenseDao saleExpenseDao;
    private final VouDiscountDao vouDiscountDao;

    @Override
    public SaleHis save(@NotNull SaleHis saleHis) {
        saleHis.setVouDate(Util1.toDateTime(saleHis.getVouDate()));
        if (Util1.isNullOrEmpty(saleHis.getKey().getVouNo())) {
            saleHis.getKey().setVouNo(getVoucherNo(saleHis.getDeptId(), saleHis.getMacId(), saleHis.getKey().getCompCode()));
        }
        List<SaleHisDetail> listSD = saleHis.getListSH();
        List<SaleDetailKey> listDel = saleHis.getListDel();
        List<SaleExpenseKey> listDelExp = saleHis.getListDelExpense();
        List<VouDiscountKey> listDelVouDiscount = saleHis.getListDelVouDiscount();
        //backup
        if (listDel != null) {
            listDel.forEach(sdDao::delete);
        }
        if (listDelExp != null) {
            listDelExp.forEach(saleExpenseDao::delete);
        }
        if (listDelVouDiscount != null) {
            listDelVouDiscount.forEach(vouDiscountDao::delete);
        }
        List<SaleExpense> listExp = saleHis.getListExpense();
        List<VouDiscount> listDiscount = saleHis.getListVouDiscount();
        //save expense
        saveSaleExpense(listExp, saleHis);
        //save detail
        saveDetail(listSD, saleHis);
        //save vou discount
        saveVouDiscount(listDiscount, saleHis);
        shDao.save(saleHis);
        saleHis.setListSH(listSD);
        return saleHis;
    }

    private void saveSaleExpense(List<SaleExpense> listExp, SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        if (listExp != null) {
            for (int i = 0; i < listExp.size(); i++) {
                SaleExpense e = listExp.get(i);
                if (Util1.getFloat(e.getAmount()) > 0) {
                    if (e.getKey().getExpenseCode() != null) {
                        if (e.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                e.getKey().setUniqueId(1);
                            } else {
                                SaleExpense pe = listExp.get(i - 1);
                                e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                            }
                        }
                        e.getKey().setVouNo(vouNo);
                        saleExpenseDao.save(e);
                    }
                }
            }
        }
    }

    private void saveDetail(List<SaleHisDetail> listSD, SaleHis sh) {
        String compCode = sh.getKey().getCompCode();
        String vouNo = sh.getKey().getVouNo();
        int depId = sh.getDeptId();
        for (int i = 0; i < listSD.size(); i++) {
            SaleHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                SaleDetailKey key = new SaleDetailKey();
                key.setCompCode(compCode);
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(depId);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        SaleHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.setTotalWeight(Util1.getFloat(cSd.getWeight()) * cSd.getQty());
                sdDao.save(cSd);
            }
        }
    }

    private void saveVouDiscount(List<VouDiscount> list, SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                VouDiscount e = list.get(i);
                if (Util1.getDouble(e.getAmount()) > 0) {
                    if (e.getKey().getUniqueId() == 0) {
                        if (i == 0) {
                            e.getKey().setUniqueId(1);
                        } else {
                            VouDiscount pe = list.get(i - 1);
                            e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                        }
                    }
                    e.getKey().setVouNo(vouNo);
                    e.getKey().setCompCode(compCode);
                    vouDiscountDao.save(e);

                }
            }
        }
    }

    @Override
    public SaleHis update(SaleHis saleHis) {
        return shDao.save(saleHis);
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return shDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public SaleHis findById(SaleHisKey id) {
        return shDao.findById(id);
    }

    @Override
    public void delete(SaleHisKey key) throws Exception {
        shDao.delete(key);
    }

    @Override
    public void restore(SaleHisKey key) throws Exception {
        shDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }


    @Override
    public List<SaleHis> unUploadVoucher(LocalDateTime syncDate) {
        return shDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<SaleHis> unUpload(String syncDate) {
        return shDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return shDao.getMaxDate();
    }


    @Override
    public void truncate(SaleHisKey key) {
        shDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return shDao.getVoucherInfo(vouDate, compCode, depId);
    }

    @Override
    public List<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        return vouDiscountDao.getVoucherDiscount(vouNo,compCode);
    }

    @Override
    public List<VouDiscount> searchDiscountDescription(String str, String compCode) {
        return vouDiscountDao.getDescription(str,compCode);
    }


}
