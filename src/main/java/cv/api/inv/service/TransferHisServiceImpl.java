package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.dao.TransferHisDao;
import cv.api.inv.dao.TransferHisDetailDao;
import cv.api.inv.entity.THDetailKey;
import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisDetail;
import cv.api.inv.entity.TransferHisKey;
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
            th.getKey().setVouNo(getVoucherNo(th.getMacId(), th.getKey().getCompCode()));
        }

        List<TransferHisDetail> listTD = th.getListTD();
        List<String> listDel = th.getDelList();
        String vouNo = th.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(detailId -> {
                if (detailId != null) {
                    detailDao.delete(detailId, th.getKey().getCompCode(), th.getKey().getDeptId());
                }
            });
        }
        for (int i = 0; i < listTD.size(); i++) {
            TransferHisDetail cSd = listTD.get(i);
            if (cSd.getStockCode() != null) {
                if (cSd.getUniqueId() == null) {
                    if (i == 0) {
                        cSd.setUniqueId(1);
                    } else {
                        TransferHisDetail pSd = listTD.get(i - 1);
                        cSd.setUniqueId(pSd.getUniqueId() + 1);
                    }
                }
                String sdCode = vouNo + "-" + cSd.getUniqueId();
                THDetailKey key = new THDetailKey();
                key.setVouNo(vouNo);
                key.setTdCode(sdCode);
                key.setDeptId(th.getKey().getDeptId());
                cSd.setKey(key);
                cSd.setCompCode(th.getKey().getCompCode());
                detailDao.save(cSd);
            }
        }
        th.setListTD(listTD);
        return dao.save(th);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "TRANSFER", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
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
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<TransferHis> search(String updatedDate, List<String> location) {
        return dao.search(updatedDate, location);
    }
}
