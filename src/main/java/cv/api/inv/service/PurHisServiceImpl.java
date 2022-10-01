/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.PurHisDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.PurDetailKey;
import cv.api.inv.entity.PurHis;
import cv.api.inv.entity.PurHisDetail;
import cv.api.inv.view.VPurchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    public PurHis save(PurHis ph) throws Exception {
        ph.setVouDate(Util1.toDateTime(ph.getVouDate()));
        if (Util1.isNullOrEmpty(ph.getVouNo())) {
            ph.setVouNo(getVoucherNo(ph.getMacId(), ph.getTrader().getKey().getCompCode()));
        }
        if (Util1.getBoolean(ph.getDeleted())) {
            phDao.save(ph);
        } else {
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
                    if (cSd.getStock().getKey().getStockCode() != null) {
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
                        cSd.setCompCode(ph.getTrader().getKey().getCompCode());
                        pdDao.save(cSd);
                    }
                }
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
    public List<PurHis> search(String fromDate, String toDate, String cusCode,
                               String vouNo, String remark, String userCode) {
        return phDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public PurHis findById(String id) {
        return phDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return phDao.delete(vouNo);
    }

    @Override
    public List<VPurchase> search(String vouNo) {
        return phDao.search(vouNo);
    }

    @Override
    public List<PurHis> unUploadVoucher(String syncDate) {
        return phDao.unUploadVoucher(syncDate);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "PURCHASE", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

}
