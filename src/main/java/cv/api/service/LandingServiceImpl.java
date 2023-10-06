package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.LandingHisDao;
import cv.api.dao.LandingHisDetailDao;
import cv.api.entity.*;
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
    private final LandingHisDetailDao gdDao;

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
        List<LandingHisDetail> listDetail = g.getListDetail();
        List<LandingHisDetailKey> listDel = g.getListDel();
        //backup
        if (listDel != null) {
            listDel.forEach(gdDao::delete);
        }

        saveGradeDetail(listDetail, g);
        g.setListDetail(listDetail);
        dao.save(g);
        return g;
    }

    private void saveGradeDetail(List<LandingHisDetail> list, LandingHis g) {
        for (int i = 0; i < list.size(); i++) {
            LandingHisDetail cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                LandingHisDetailKey key = new LandingHisDetailKey();
                key.setCompCode(g.getKey().getCompCode());
                key.setVouNo(g.getKey().getVouNo());
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        LandingHisDetail pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                if (cSd.getTotalWeight() == 0) cSd.setTotalWeight(cSd.getWeight() * cSd.getQty());
                cSd.setDeptId(g.getDeptId());
                gdDao.save(cSd);
            }
        }
    }

//    private void saveGRNDetailFormula(List<GRNDetailFormula> list, GRN g) {
//        for (int i = 0; i < list.size(); i++) {
//            GRNDetailFormula cSd = list.get(i);
//            if (Util1.isNullOrEmpty(cSd.getKey())) {
//                GRNDetailFormulaKey key = new GRNDetailFormulaKey();
//                key.setCompCode(g.getKey().getCompCode());
//                key.setVouNo(g.getKey().getVouNo());
//                key.setUniqueId(0);
//                cSd.setKey(key);
//            }
//            if (Util1.isNullOrEmpty(cSd.getDescription() != null)) {
//                if (cSd.getKey().getUniqueId() == 0) {
//                    if (i == 0) {
//                        cSd.getKey().setUniqueId(1);
//                    } else {
//                        GRNDetailFormula pSd = list.get(i - 1);
//                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
//                    }
//                }
//                gdfDao.save(cSd);
//            }
//        }
//    }

    @Override
    public List<LandingHis> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<LandingHis> search(String compCode, Integer deptId) {
        return dao.search(compCode, deptId);
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
    public boolean open(LandingHisKey key) {
        return dao.open(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "Grade", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    // grade detail

    @Override
    public LandingHisDetail save(LandingHisDetail b) {
        return gdDao.save(b);
    }

    @Override
    public void delete(LandingHisDetailKey key) {
        gdDao.delete(key);
    }

    @Override
    public List<LandingHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        return gdDao.search(vouNo, compCode, deptId);
    }
}
