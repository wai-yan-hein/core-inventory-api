package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.SeqTableDao;
import cv.api.dao.PurOrderHisDao;
import cv.api.dao.PurOrderHisDetailDao;
import cv.api.entity.PurOrderHisDetail;
import cv.api.entity.PurOrderHisDetailKey;
import cv.api.entity.PurOrderHis;
import cv.api.entity.PurOrderHisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PurOrderHisServiceImpl implements PurOrderHisService {
    private final PurOrderHisDao dao;
    public final PurOrderHisDetailDao detailDao;
    private final SeqTableDao seqDao;


    @Override
    public PurOrderHis save(PurOrderHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getDeptId(), obj.getMacId(), obj.getKey().getCompCode()));
        }
        String vouNo = obj.getKey().getVouNo();
        String compCode = obj.getKey().getCompCode();
        //delete detail
        boolean delete = detailDao.deletePurOrderHisDetail(vouNo, compCode);
        if (delete) {
            List<PurOrderHisDetail> listDetail = obj.getListPurOrderHisDetail();
            for (int i = 0; i < listDetail.size(); i++) {
                PurOrderHisDetail cSd = listDetail.get(i);
                if (cSd.getStockCode() != null) {
                    if (Util1.isNullOrEmpty(cSd.getKey())) {
                        PurOrderHisDetailKey key = new PurOrderHisDetailKey();
                        key.setCompCode(obj.getKey().getCompCode());
                        key.setVouNo(vouNo);
                        key.setUniqueId(0);
                        cSd.setDeptId(obj.getDeptId());
//                        cSd.setLocCode(obj.getLocation());
                        cSd.setKey(key);
                    }
                    double weight = cSd.getWeight();
                    if (weight > 0) {
                        if (cSd.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                cSd.getKey().setUniqueId(1);
                            } else {
                                PurOrderHisDetail pSd = listDetail.get(i - 1);
                                cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                            }
                        }
                    }
                    detailDao.save(cSd);
                }

//                obj.setListIRDetail(listDetail);
            }
            dao.save(obj);
        }
        return obj;
    }


    @Override
    public PurOrderHis findById(PurOrderHisKey key) {
        return dao.findById(key);
    }

    @Override
    public boolean delete(PurOrderHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(PurOrderHisKey key) {
        return dao.restore(key);
    }

    @Override
    public PurOrderHisDetail save(PurOrderHisDetail obj) {
        return detailDao.save(obj);
    }

    @Override
    public boolean delete(PurOrderHisDetailKey key) {
        return detailDao.delete(key);
    }
    @Override
    public List<PurOrderHisDetail> getPurOrderHisDetail(String vouNo, String compCode) {
        return detailDao.getPurOrderHisDetail(vouNo, compCode);
    }
    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "ISSREC", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
