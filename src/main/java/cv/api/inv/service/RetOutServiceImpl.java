/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.RetOutDao;
import cv.api.inv.dao.RetOutDetailDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.RetOutHis;
import cv.api.inv.entity.RetOutHisDetail;
import cv.api.inv.entity.RetOutHisKey;
import cv.api.inv.entity.RetOutKey;
import cv.api.inv.view.VReturnOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wai Yan
 */
@Service
@Transactional
@Slf4j
public class RetOutServiceImpl implements RetOutService {

    @Autowired
    private RetOutDao rDao;
    @Autowired
    private RetOutDetailDao rd;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public RetOutHis save(RetOutHis rin) throws Exception {
        rin.setVouDate(Util1.toDateTime(rin.getVouDate()));
        if (Util1.isNullOrEmpty(rin.getKey().getVouNo())) {
            rin.getKey().setVouNo(getVoucherNo(rin.getMacId(), rin.getKey().getCompCode()));
        }
        List<RetOutHisDetail> listSD = rin.getListRD();
        List<String> listDel = rin.getListDel();
        String vouNo = rin.getKey().getVouNo();
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
            if (cSd.getStockCode() != null) {
                if (cSd.getUniqueId() == null) {
                    if (i == 0) {
                        cSd.setUniqueId(1);
                    } else {
                        RetOutHisDetail pSd = listSD.get(i - 1);
                        cSd.setUniqueId(pSd.getUniqueId() + 1);
                    }
                }
                String sdCode = vouNo + "-" + cSd.getUniqueId();
                cSd.setRoKey(new RetOutKey(sdCode, vouNo, rin.getKey().getDeptId()));
                cSd.setCompCode(rin.getKey().getCompCode());
                rd.save(cSd);
            }
        }
        rin.setIntgUpdStatus(null);
        rDao.save(rin);
        rin.setListRD(listSD);
        return rin;
    }

    @Override
    public RetOutHis update(RetOutHis ro) {
        return rDao.save(ro);
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return rDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public RetOutHis findById(RetOutHisKey id) {
        return rDao.findById(id);
    }

    @Override
    public void delete(RetOutHisKey key) throws Exception {
        rDao.delete(key);
    }

    @Override
    public List<VReturnOut> search(String vouNo) {
        return rDao.search(vouNo);
    }

    @Override
    public List<RetOutHis> unUploadVoucher(String syncDate) {
        return rDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<RetOutHis> unUpload() {
        return rDao.unUpload();
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "RETURN_OUT", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

}
