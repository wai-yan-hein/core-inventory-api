/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.MessageSender;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.common.Voucher;
import com.cv.inv.api.dao.RetOutDao;
import com.cv.inv.api.dao.RetOutDetailDao;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.entity.*;
import com.cv.inv.api.view.VReturnOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wai Yan
 */
@Service
@Transactional
public class RetOutServiceImpl implements RetOutService {

    @Autowired
    private RetOutDao rDao;
    @Autowired
    private RetOutDetailDao rd;
    @Autowired
    private SeqTableDao seqDao;
    @Autowired
    private MessageSender messageSender;

    @Override
    public RetOutHis save(RetOutHis rin) throws Exception {
        if (Util1.getBoolean(rin.getDeleted())) {
            rDao.save(rin);
        } else {
            List<RetOutHisDetail> listSD = rin.getListRD();
            List<String> listDel = rin.getListDel();
            String vouNo = rin.getVouNo();
            if (rin.getStatus().equals("NEW")) {
                RetOutHis valid = rDao.findById(vouNo);
                if (valid != null) {
                    throw new IllegalStateException("Duplicate Return Out Voucher");
                }
            }
            if (listDel != null) {
                listDel.forEach(detailId -> {
                    if (detailId != null) {
                        try {
                            rd.delete(detailId);
                        } catch (Exception ex) {
                            throw new IllegalStateException(String.format("Return Out Delete : %s", ex.getMessage()));
                        }
                    }
                });
            }
            for (int i = 0; i < listSD.size(); i++) {
                RetOutHisDetail cSd = listSD.get(i);
                if (cSd.getStock() != null) {
                    if (cSd.getStock().getStockCode() != null) {
                        if (cSd.getUniqueId() == null) {
                            if (i == 0) {
                                cSd.setUniqueId(1);
                            } else {
                                RetOutHisDetail pSd = listSD.get(i - 1);
                                cSd.setUniqueId(pSd.getUniqueId() + 1);
                            }
                        }
                        String sdCode = vouNo + "-" + cSd.getUniqueId();
                        cSd.setRoKey(new RetOutKey(sdCode, vouNo));
                        rd.save(cSd);
                    }
                }
            }
            rDao.save(rin);
            rin.setListRD(listSD);
            updateVoucher(rin.getCompCode(), rin.getMacId(), Voucher.RETOUT.name());
            messageSender.sendMessage("RETURN_OUT", vouNo);
        }
        return rin;
    }

    @Override
    public RetOutHis update(RetOutHis ro) {
        return rDao.save(ro);
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusCode,
                                  String vouNo, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, userCode);
    }

    @Override
    public RetOutHis findById(String id) {
        return rDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return rDao.delete(vouNo);
    }

    @Override
    public List<VReturnOut> search(String vouNo) {
        return rDao.search(vouNo);
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
