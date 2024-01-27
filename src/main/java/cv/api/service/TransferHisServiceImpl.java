package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.SeqTableDao;
import cv.api.dao.TransferHisDao;
import cv.api.dao.TransferHisDetailDao;
import cv.api.entity.THDetailKey;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisDetail;
import cv.api.entity.TransferHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TransferHisServiceImpl implements TransferHisService {
    @Autowired
    private TransferHisDao dao;
    @Autowired
    private TransferHisDetailDao detailDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public TransferHis save(TransferHis th) {
        th.setVouDate(Util1.toDateTime(th.getVouDate()));
        if (Util1.isNullOrEmpty(th.getKey().getVouNo())) {
            th.getKey().setVouNo(getVoucherNo(th.getDeptId(), th.getMacId(), th.getKey().getCompCode()));
        }

        List<TransferHisDetail> listTD = th.getListTD();
        List<THDetailKey> listDel = th.getDelList();
        String vouNo = th.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> detailDao.delete(key));
        }
        for (int i = 0; i < listTD.size(); i++) {
            TransferHisDetail cSd = listTD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                THDetailKey key = new THDetailKey();
                key.setCompCode(th.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(th.getDeptId());
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        TransferHisDetail pSd = listTD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.setTotalWeight(Util1.getFloat(cSd.getWeight()) * cSd.getQty());
                detailDao.save(cSd);
            }
        }
        th.setListTD(listTD);
        return dao.save(th);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "TRANSFER", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        return dao.findById(key);
    }

    @Override
    public List<TransferHis> unUpload(String syncDate) {
        return dao.unUpload(syncDate);
    }

    @Override
    public void delete(TransferHisKey key) {
        dao.delete(key);
    }

    @Override
    public void restore(TransferHisKey key) {
        dao.restore(key);
    }


    @Override
    public List<TransferHis> search(String updatedDate, List<String> location) {
        return dao.search(updatedDate, location);
    }

    @Override
    public void truncate(TransferHisKey key) {
        dao.truncate(key);
    }
}
