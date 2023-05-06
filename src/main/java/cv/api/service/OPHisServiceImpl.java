package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.OPHisDao;
import cv.api.dao.OPHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.LocationKey;
import cv.api.entity.OPHis;
import cv.api.entity.OPHisDetail;
import cv.api.entity.OPHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
            op.getKey().setVouNo(getVoucherNo(op.getKey().getDeptId(), op.getMacId(), op.getKey().getCompCode()));
        }
        List<OPHisDetail> listSD = op.getDetailList();
        List<String> listDel = op.getListDel();
        String vouNo = op.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(detailId -> {
                if (detailId != null) {
                    opHisDetailDao.delete(detailId, op.getKey().getCompCode(), op.getKey().getDeptId());
                }
            });
        }
        for (int i = 0; i < listSD.size(); i++) {
            OPHisDetail cSd = listSD.get(i);
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        OPHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                String opCode = vouNo + "-" + cSd.getKey().getUniqueId();
                cSd.getKey().setOpCode(opCode);
                cSd.getKey().setVouNo(vouNo);
                cSd.getKey().setCompCode(op.getKey().getCompCode());
                cSd.getKey().setDeptId(op.getKey().getDeptId());
                opHisDetailDao.save(cSd);
            }
        }
        opHisDao.save(op);
        op.setDetailList(listSD);
        return op;
    }

    @Override
    public OPHis findByCode(OPHisKey key) {
        return opHisDao.findByCode(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "OPENING", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public List<OPHis> search(String compCode) {
        return opHisDao.search(compCode);
    }

    @Override
    public List<OPHis> unUpload() {
        return opHisDao.unUpload();
    }

    @Override
    public void delete(OPHisKey key) {
        opHisDao.delete(key);
    }

    @Override
    public List<OPHis> search(String updatedDate, List<LocationKey> keys) {
        return opHisDao.search(updatedDate, keys);
    }

    @Override
    public Date getMaxDate() {
        return opHisDao.getMaxDate();
    }

}
