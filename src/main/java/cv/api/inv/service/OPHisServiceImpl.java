package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.OPHisDao;
import cv.api.inv.dao.OPHisDetailDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisDetail;
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
        if (Util1.isNullOrEmpty(op.getVouNo())) {
            op.setVouNo(getVoucherNo(op.getMacId(), op.getCompCode()));
        }
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
        }
        return opHisDao.save(op);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyyyy");
        int seqNo = seqDao.getSequence(macId, "OPENING", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
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
