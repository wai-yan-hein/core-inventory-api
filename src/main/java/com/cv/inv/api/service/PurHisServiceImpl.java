/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.common.Voucher;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.entity.PurHis;
import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cv.inv.api.dao.PurHisDao;
import com.cv.inv.api.entity.PurDetailKey;
import com.cv.inv.api.entity.PurHisDetail;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Mg Kyaw Thura Aung
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

    @Override
    public PurHis save(PurHis ph) throws Exception {
        List<PurHisDetail> listSD = ph.getListPD();
        List<String> listDel = ph.getListDel();
        String vouNo = ph.getVouNo();
        if (ph.getStatus().equals("NEW")) {
            PurHis valid = phDao.findById(vouNo);
            if (valid != null) {
                throw new IllegalStateException("Duplicate Purchase Voucher");
            }
        }
        if (listDel != null) {
            listDel.forEach(detailId -> {
                if (detailId != null) {
                    try {
                        pdDao.delete(detailId);
                    } catch (Exception ex) {
                        log.error("listDel : " + ex.getMessage());
                    }
                }
            });
        }
        for (int i = 0; i < listSD.size(); i++) {
            PurHisDetail cSd = listSD.get(i);
            if (cSd.getStock() != null) {
                if (cSd.getStock().getStockCode() != null) {
                    if (cSd.getUniqueId() == null) {
                        if (i == 0) {
                            cSd.setUniqueId(1);
                        } else {
                            PurHisDetail pSd = listSD.get(i - 1);
                            cSd.setUniqueId(pSd.getUniqueId() + 1);
                        }
                    }
                    String sdCode = vouNo + "-" + cSd.getUniqueId();
                    cSd.setPdKey(new PurDetailKey(vouNo, sdCode));
                    pdDao.save(cSd);
                }
            }
        }
        phDao.save(ph);
        ph.setListPD(listSD);
        updateVoucher(ph.getCompCode(), ph.getMacId(), Voucher.PURCHASE.name());
        return ph;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode) {
        return phDao.search(fromDate, toDate, cusCode, vouNo, userCode);
    }

    @Override
    public PurHis findById(String id) {
        return phDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return phDao.delete(vouNo);
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
