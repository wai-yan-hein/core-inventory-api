/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.SaleHisDao;
import cv.api.dao.SaleHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisDetail;
import cv.api.entity.SaleHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
public class SaleHisServiceImpl implements SaleHisService {

    @Autowired
    private SaleHisDao shDao;
    @Autowired
    private SaleHisDetailDao sdDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public SaleHis save(SaleHis saleHis) {
        saleHis.setVouDate(Util1.toDateTime(saleHis.getVouDate()));
        if (Util1.isNullOrEmpty(saleHis.getKey().getVouNo())) {
            saleHis.getKey().setVouNo(getVoucherNo(saleHis.getKey().getDeptId(), saleHis.getMacId(), saleHis.getKey().getCompCode()));
        }
        List<SaleHisDetail> listSD = saleHis.getListSH();
        List<SaleDetailKey> listDel = saleHis.getListDel();
        String vouNo = saleHis.getKey().getVouNo();
        //backup
        if (listDel != null) {
            listDel.forEach(key -> sdDao.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            SaleHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                SaleDetailKey key = new SaleDetailKey();
                key.setDeptId(saleHis.getKey().getDeptId());
                key.setCompCode(saleHis.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        SaleHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                sdDao.save(cSd);
            }
        }
        shDao.save(saleHis);
        saleHis.setListSH(listSD);
        return saleHis;
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
    public List<SaleHis> unUploadVoucher(String syncDate) {
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


}
