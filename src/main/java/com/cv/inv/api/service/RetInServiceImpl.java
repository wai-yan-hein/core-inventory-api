/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.common.Voucher;
import com.cv.inv.api.dao.RetInDao;
import com.cv.inv.api.dao.RetInDetailDao;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.entity.RetInHis;
import com.cv.inv.api.entity.RetInHisDetail;
import com.cv.inv.api.entity.RetInKey;
import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wai Yan
 */
@Service
@Transactional
public class RetInServiceImpl implements RetInService {

    @Autowired
    private RetInDao rDao;
    @Autowired
    private RetInDetailDao sdDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public RetInHis save(RetInHis rin) throws Exception {
        List<RetInHisDetail> listSD = rin.getListRD();
        List<String> listDel = rin.getListDel();
        String vouNo = rin.getVouNo();
        if (rin.getStatus().equals("NEW")) {
            RetInHis valid = rDao.findById(vouNo);
            if (valid != null) {
                throw new IllegalStateException("Duplicate Sale Voucher");
            }
        }
        if (listDel != null) {
            listDel.forEach(detailId -> {
                if (detailId != null) {
                    try {
                        sdDao.delete(detailId);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
        for (int i = 0; i < listSD.size(); i++) {
            RetInHisDetail cSd = listSD.get(i);
            if (cSd.getStock() != null) {
                if (cSd.getStock().getStockCode() != null) {
                    if (cSd.getUniqueId() == null) {
                        if (i == 0) {
                            cSd.setUniqueId(1);
                        } else {
                            RetInHisDetail pSd = listSD.get(i - 1);
                            cSd.setUniqueId(pSd.getUniqueId() + 1);
                        }
                    }
                    String sdCode = vouNo + "-" + cSd.getUniqueId();
                    cSd.setRiKey(new RetInKey(sdCode, vouNo));
                    sdDao.save(cSd);
                }
            }
        }
        rDao.save(rin);
        rin.setListRD(listSD);
        updateVoucher(rin.getCompCode(), rin.getMacId(), Voucher.RETIN.name());
        return rin;
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, userCode);
    }

    @Override
    public RetInHis findById(String id) {
        return rDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return rDao.delete(vouNo);
    }

    private void updateVoucher(String compCode, Integer macId, String option) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyyyy");
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(macId);
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable last = seqDao.findById(key);
        if (last == null) {
            last = new SeqTable();
            last.setKey(key);
            last.setSeqNo(2);
        } else {
            last.setSeqNo(Util1.getInteger(last.getSeqNo()) + 1);
        }
        seqDao.save(last);
    }

}
