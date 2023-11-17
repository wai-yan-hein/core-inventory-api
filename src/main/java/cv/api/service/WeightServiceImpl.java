package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.SeqTableDao;
import cv.api.dao.WeightDao;
import cv.api.dao.WeightDetailDao;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import cv.api.entity.WeightHisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeightServiceImpl implements WeightService {
    private final WeightDao dao;
    public final WeightDetailDao detailDao;
    private final SeqTableDao seqDao;


    @Override
    public WeightHis save(WeightHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getDeptId(), obj.getMacId(), obj.getKey().getCompCode()));
        }
        List<WeightHisDetail> listDetail = obj.getListDetail();
        String vouNo = obj.getKey().getVouNo();
        for (int i = 0; i < listDetail.size(); i++) {
            WeightHisDetail cSd = listDetail.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                WeightHisDetailKey key = new WeightHisDetailKey();
                key.setCompCode(obj.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            double weight = cSd.getWeight();
            if (weight > 0) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        WeightHisDetail pSd = listDetail.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
            }
            detailDao.save(cSd);
            dao.save(obj);
            obj.setListDetail(listDetail);
        }
        return obj;
    }


    @Override
    public WeightHis findById(WeightHisKey key) {
        return dao.findById(key);
    }

    @Override
    public boolean delete(WeightHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(WeightHisKey key) {
        return dao.restore(key);
    }

    @Override
    public WeightHisDetail save(WeightHisDetail obj) {
        return detailDao.save(obj);
    }

    @Override
    public boolean delete(WeightHisDetailKey key) {
        return detailDao.delete(key);
    }

    @Override
    public List<WeightHis> getWeightHistory(String fromDate, String toDate, String traderCode,
                                            String stockCode, String vouNo, String remark,
                                            boolean deleted, String compCode,String tranSource) {
        return dao.getWeightHistory(fromDate, toDate, traderCode, stockCode, vouNo, remark, deleted, compCode,tranSource);
    }

    @Override
    public List<WeightHisDetail> getWeightDetail(String vouNo, String compCode) {
        return dao.getWeightDetail(vouNo, compCode);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
