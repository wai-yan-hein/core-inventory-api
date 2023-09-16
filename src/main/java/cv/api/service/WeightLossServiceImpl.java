package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.SeqTableDao;
import cv.api.dao.WeightLossDao;
import cv.api.dao.WeightLossHisDetailDao;
import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisDetail;
import cv.api.entity.WeightLossHisDetailKey;
import cv.api.entity.WeightLossHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WeightLossServiceImpl implements WeightLossService {
    @Autowired
    private WeightLossDao dao;
    @Autowired
    private WeightLossHisDetailDao detailDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public WeightLossHis save(WeightLossHis l) {
        l.setVouDate(Util1.toDateTime(l.getVouDate()));
        if (Util1.isNullOrEmpty(l.getKey().getVouNo())) {
            l.getKey().setVouNo(getVoucherNo(l.getDeptId(),l.getMacId(), l.getKey().getCompCode()));
        }
        String vouNo = l.getKey().getVouNo();
        List<WeightLossHisDetailKey> delKeys = l.getDelKeys();
        if(delKeys != null) {
            delKeys.forEach(key -> {
                detailDao.delete(key);
            });
        }

        List<WeightLossHisDetail> list = l.getListDetail();
        for (int i = 0; i < list.size(); i++) {
            WeightLossHisDetail cSd = list.get(i);
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        WeightLossHisDetail pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.getKey().setVouNo(vouNo);
                detailDao.save(cSd);
            }
        }
        return dao.save(l);
    }

    @Override
    public WeightLossHis findById(WeightLossHisKey key) {
        return dao.findById(key);
    }

    @Override
    public void delete(WeightLossHisKey key) {
        dao.delete(key);
    }

    @Override
    public void restore(WeightLossHisKey key) {
        dao.restore(key);
    }

    @Override
    public List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId) {
        return dao.search(fromDate, toDate, locCode, compCode, deptId);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "WEIGHT", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
