package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.OPHisDao;
import cv.api.inv.dao.OPHisDetailDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisDetail;
import cv.api.inv.entity.OPHisKey;
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
        if (Util1.isNullOrEmpty(op.getKey().getVouNo())) {
            op.getKey().setVouNo(getVoucherNo(op.getMacId(), op.getKey().getCompCode()));
        }
        if (op.isDeleted()) {
            opHisDao.save(op);
        } else {
            List<OPHisDetail> listSD = op.getDetailList();
            List<String> listDel = op.getListDel();
            String vouNo = op.getKey().getVouNo();
            if (listDel != null) {
                listDel.forEach(detailId -> {
                    if (detailId != null) {
                        opHisDetailDao.delete(detailId);
                    }
                });
            }
            for (int i = 0; i < listSD.size(); i++) {
                OPHisDetail cSd = listSD.get(i);
                if (cSd.getStockCode() != null) {
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
                    cSd.setCompCode(op.getKey().getCompCode());
                    cSd.setDeptId(op.getKey().getDeptId());
                    opHisDetailDao.save(cSd);
                }
            }
            opHisDao.save(op);
            op.setDetailList(listSD);
        }
        return opHisDao.save(op);
    }

    @Override
    public OPHis findByCode(OPHisKey key) {
        return opHisDao.findByCode(key);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "OPENING", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<OPHis> search(String compCode) {
        return opHisDao.search(compCode);
    }

}
