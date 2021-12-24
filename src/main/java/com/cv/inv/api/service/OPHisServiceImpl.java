package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.common.Voucher;
import com.cv.inv.api.dao.OPHisDao;
import com.cv.inv.api.dao.OPHisDetailDao;
import com.cv.inv.api.dao.SeqTableDao;
import com.cv.inv.api.entity.OPHis;
import com.cv.inv.api.entity.OPHisDetail;
import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OPHisServiceImpl implements OPHisService {
    @Autowired
    private OPHisDao opHisDao;
    @Autowired
    private OPHisDetailDao opHisDetailDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public OPHis save(OPHis op) {
        if (op.isDeleted()) {
            opHisDao.save(op);
        } else {
            List<OPHisDetail> listSD = op.getDetailList();
            List<String> listDel = op.getListDel();
            String vouNo = op.getVouNo();
            if (op.getStatus().equals("NEW")) {
                OPHis valid = opHisDao.findByCode(vouNo);
                if (valid != null) {
                    throw new IllegalStateException("Duplicate Opening Voucher");
                }
            }
            if (listDel != null) {
                listDel.forEach(detailId -> {
                    if (detailId != null) {
                        opHisDetailDao.delete(detailId);
                    }
                });
            }
            for (int i = 0; i < listSD.size(); i++) {
                OPHisDetail cSd = listSD.get(i);
                if (cSd.getStock() != null) {
                    if (cSd.getUniqueId() == null) {
                        if (i == 0) {
                            cSd.setUniqueId(1);
                        } else {
                            OPHisDetail pSd = listSD.get(i - 1);
                            cSd.setUniqueId(pSd.getUniqueId() + 1);
                        }
                    }
                    String opCode = vouNo + "-" + cSd.getUniqueId();
                    cSd.setOpCode(opCode);
                    cSd.setVouNo(vouNo);
                    opHisDetailDao.save(cSd);
                }
            }
            opHisDao.save(op);
            op.setDetailList(listSD);
            updateVoucher(op.getCompCode(), op.getMacId(), Voucher.OPENING.name());
        }
        return opHisDao.save(op);
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

    @Override
    public List<OPHis> search(String compCode) {
        return opHisDao.search(compCode);
    }

    @Override
    public List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode) {
        return opHisDao.search(fromDate, toDate, vouNo, userCode, compCode);
    }
}
