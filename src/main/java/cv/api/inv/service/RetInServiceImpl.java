/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.RetInDao;
import cv.api.inv.dao.RetInDetailDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.RetInHis;
import cv.api.inv.entity.RetInHisDetail;
import cv.api.inv.entity.RetInKey;
import cv.api.inv.view.VReturnIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
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
        rin.setVouDate(Util1.toDateTime(rin.getVouDate()));
        if (Util1.isNullOrEmpty(rin.getKey().getVouNo())) {
            rin.getKey().setVouNo(getVoucherNo(rin.getMacId(), rin.getKey().getCompCode()));
        }
        if (Util1.getBoolean(rin.getDeleted())) {
            rDao.save(rin);
        } else {
            List<RetInHisDetail> listSD = rin.getListRD();
            List<String> listDel = rin.getListDel();
            String vouNo = rin.getKey().getVouNo();
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
                if (cSd.getStockCode() != null) {
                    if (cSd.getStockCode() != null) {
                        if (cSd.getUniqueId() == null) {
                            if (i == 0) {
                                cSd.setUniqueId(1);
                            } else {
                                RetInHisDetail pSd = listSD.get(i - 1);
                                cSd.setUniqueId(pSd.getUniqueId() + 1);
                            }
                        }
                        String sdCode = vouNo + "-" + cSd.getUniqueId();
                        cSd.setRiKey(new RetInKey(sdCode, vouNo, rin.getKey().getDeptId()));
                        cSd.setCompCode(rin.getKey().getCompCode());
                        sdDao.save(cSd);
                    }
                }
            }
            rDao.save(rin);
            rin.setListRD(listSD);
        }
        return rin;
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "RETURN_IN", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public RetInHis update(RetInHis ri) {
        return rDao.save(ri);
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode,
                                 String vouNo, String remark, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public RetInHis findById(String id) {
        return rDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return rDao.delete(vouNo);
    }

    @Override
    public List<VReturnIn> search(String vouNo) {
        return rDao.search(vouNo);
    }

    @Override
    public List<RetInHis> unUploadVoucher(String syncDate) {
        return rDao.unUploadVoucher(syncDate);
    }


}
