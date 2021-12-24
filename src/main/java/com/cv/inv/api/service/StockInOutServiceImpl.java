/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.dao.StockInOutDao;
import com.cv.inv.api.dao.StockInOutDetailDao;
import com.cv.inv.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cv.inv.api.common.Voucher.STOCKINOUT;

/**
 * @author Lenovo
 */
@Service
@Transactional
public class StockInOutServiceImpl implements StockInOutService {

    @Autowired
    private StockInOutDao ioDao;
    @Autowired
    private StockInOutDetailDao iodDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public StockInOut save(StockInOut io) throws Exception {
        if (Util1.getBoolean(io.getDeleted())) {
            ioDao.save(io);
        } else {
            List<StockInOutDetail> listSD = io.getListSH();
            List<String> listDel = io.getListDel();
            String vouNo = io.getVouNo();
            if (io.getStatus().equals("NEW")) {
                StockInOut valid = ioDao.findById(vouNo);
                if (valid != null) {
                    throw new IllegalStateException("Duplicate Stock In/Out Voucher");
                }
            }
            if (listDel != null) {
                listDel.forEach(detailId -> {
                    if (detailId != null) {
                        iodDao.delete(detailId);
                    }
                });
            }
            for (int i = 0; i < listSD.size(); i++) {
                StockInOutDetail cSd = listSD.get(i);
                if (cSd.getStock() != null) {
                    if (cSd.getStock().getStockCode() != null) {
                        if (cSd.getUniqueId() == null) {
                            if (i == 0) {
                                cSd.setUniqueId(1);
                            } else {
                                StockInOutDetail pSd = listSD.get(i - 1);
                                cSd.setUniqueId(pSd.getUniqueId() + 1);
                            }
                        }
                        String sdCode = vouNo + "-" + cSd.getUniqueId();
                        cSd.setIoKey(new StockInOutKey(sdCode, vouNo));
                        iodDao.save(cSd);
                    }
                }
            }
            ioDao.save(io);
            io.setListSH(listSD);
            updateVoucher(io.getCompCode(), io.getMacId(), STOCKINOUT.name());
        }
        return io;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                                   String vouNo, String userCode) {
        return ioDao.search(fromDate, toDate, remark, desp, vouNo, userCode);
    }

    @Override
    public StockInOut findById(String id) {
        return ioDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return ioDao.delete(vouNo);
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
