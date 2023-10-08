package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.LandingHisCriteriaDao;
import cv.api.dao.LandingHisDao;
import cv.api.entity.LandingHis;
import cv.api.entity.LandingHisCriteria;
import cv.api.entity.LandingHisCriteriaKey;
import cv.api.entity.LandingHisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LandingServiceImpl implements LandingService {
    private final SeqTableService seqTableService;
    private final LandingHisDao dao;
    private final LandingHisCriteriaDao criteriaDao;

    @Override
    public LandingHis findByCode(LandingHisKey key) {
        return dao.findByCode(key);
    }

    @Override
    public LandingHis save(LandingHis g) {
        g.setVouDate(Util1.toDateTime(g.getVouDate()));
        if (Util1.isNullOrEmpty(g.getKey().getVouNo())) {
            g.getKey().setVouNo(getVoucherNo(g.getDeptId(), g.getMacId(), g.getKey().getCompCode()));
        }
        List<LandingHisCriteriaKey> listDel = g.getListDel();
        if (listDel != null) listDel.forEach(criteriaDao::delete);
        List<LandingHisCriteria> listDetail = g.getListDetail();
        //backup
        saveLandingDetail(listDetail, g);
        g.setListDetail(listDetail);
        dao.save(g);
        return g;
    }

    private void saveLandingDetail(List<LandingHisCriteria> list, LandingHis g) {
        for (int i = 0; i < list.size(); i++) {
            LandingHisCriteria cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                LandingHisCriteriaKey key = new LandingHisCriteriaKey();
                key.setCompCode(g.getKey().getCompCode());
                key.setVouNo(g.getKey().getVouNo());
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            if (cSd.getCriteriaCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        LandingHisCriteria pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                criteriaDao.save(cSd);
            }
        }
    }


    @Override
    public List<LandingHis> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public boolean delete(LandingHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(LandingHisKey key) {
        return dao.delete(key);
    }

    @Override
    public List<LandingHisCriteria> getLandingHisCriteria(String vouNo, String compCode) {
        return criteriaDao.getLandingDetailCriteria(vouNo, compCode);
    }

    @Override
    public List<LandingHis> getLandingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                              String userCode, String stockCode, String locCode, String compCode, Integer deptId, boolean deleted) {
        return dao.getLandingHistory(fromDate, toDate, traderCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted);
    }


    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "Landing", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
