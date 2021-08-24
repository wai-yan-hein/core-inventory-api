/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.DuplicateException;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.SaleDetailDao;
import com.cv.inv.api.dao.SaleHisDao;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.entity.SaleDetailKey;
import com.cv.inv.api.entity.SaleHis;
import com.cv.inv.api.entity.SaleHisDetail;
import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleHisServiceImpl implements SaleHisService {

    @Autowired
    private SaleHisDao shDao;
    @Autowired
    private SaleDetailDao sdDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public SaleHis save(SaleHis saleHis) throws Exception {
        List<SaleHisDetail> listSD = saleHis.getListSH();
        List<String> listDel = saleHis.getListDel();
        String vouNo = saleHis.getVouNo();
        if (saleHis.getStatus().equals("NEW")) {
            SaleHis valid = shDao.findById(vouNo);
            if (valid != null) {
                throw new DuplicateException("Duplicate Slae Voucher");
            }
        }
        if (listDel != null) {
            listDel.forEach(detailId -> {
                if (detailId != null) {
                    sdDao.delete(detailId);
                }
            });
        }
        for (int i = 0; i < listSD.size(); i++) {
            SaleHisDetail cSd = listSD.get(i);
            if (cSd.getUniqueId() == null) {
                if (i == 0) {
                    cSd.setUniqueId(1);
                } else {
                    SaleHisDetail pSd = listSD.get(i - 1);
                    cSd.setUniqueId(pSd.getUniqueId() + 1);
                }
            }
        }

        listSD.stream().filter(sd -> (sd.getStock() != null)).filter(sd -> (sd.getSdKey() == null)).map(sd -> {
            String sdCode = vouNo + '-' + sd.getUniqueId();
            sd.setSdKey(new SaleDetailKey(vouNo, sdCode));
            return sd;
        }).forEachOrdered(sd -> {
            sdDao.save(sd);
        });
        shDao.save(saleHis);
        saleHis.setListSH(listSD);
        updateVoucher(saleHis.getCompCode(), saleHis.getMacId(), "Sale");
        return saleHis;
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode) {
        return shDao.search(fromDate, toDate, cusCode, vouNo, userCode);
    }

    @Override
    public SaleHis findById(String id) {
        return shDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return shDao.delete(vouNo);
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
