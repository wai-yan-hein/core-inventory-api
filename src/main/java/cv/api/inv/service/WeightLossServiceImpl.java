package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.dao.WeightLossDao;
import cv.api.inv.dao.WeightLossHisDetailDao;
import cv.api.inv.entity.WeightLossHis;
import cv.api.inv.entity.WeightLossHisDetail;
import cv.api.inv.entity.WeightLossHisDetailKey;
import cv.api.inv.entity.WeightLossHisKey;
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
            l.getKey().setVouNo(getVoucherNo(l.getMacId(), l.getKey().getCompCode()));
        }
        String vouNo = l.getKey().getVouNo();
        List<WeightLossHisDetailKey> delKeys = l.getDelKeys();
        delKeys.forEach(key -> {
            detailDao.delete(key);
        });
        List<WeightLossHisDetail> list = l.getListDetail();
        for (int i = 0; i < list.size(); i++) {
            WeightLossHisDetail cSd = list.get(i);
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
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

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "WEIGHT", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
