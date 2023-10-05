package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.GRNDao;
import cv.api.dao.GRNDetailDao;
import cv.api.dao.GradeHisDao;
import cv.api.dao.GradeHisDetailDao;
import cv.api.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final SeqTableService seqTableService;
    private final GradeHisDao dao;
    private final GradeHisDetailDao gdDao;

    @Override
    public GradeHis findByCode(GradeHisKey key) {
        return dao.findByCode(key);
    }

    @Override
    public GradeHis save(GradeHis g) {
        g.setVouDate(Util1.toDateTime(g.getVouDate()));
        if (Util1.isNullOrEmpty(g.getKey().getVouNo())) {
            g.getKey().setVouNo(getVoucherNo(g.getDeptId(), g.getMacId(), g.getKey().getCompCode()));
        }
        List<GradeHisDetail> listDetail = g.getListDetail();
        List<GradeHisDetailKey> listDel = g.getListDel();
        //backup
        if (listDel != null) {
            listDel.forEach(gdDao::delete);
        }

        saveGradeDetail(listDetail, g);
        g.setListDetail(listDetail);
        dao.save(g);
        return g;
    }

    private void saveGradeDetail(List<GradeHisDetail> list, GradeHis g) {
        for (int i = 0; i < list.size(); i++) {
            GradeHisDetail cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                GradeHisDetailKey key = new GradeHisDetailKey();
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
                        GradeHisDetail pSd = list.get(i - 1);
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
    public List<GradeHis> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<GradeHis> search(String compCode, Integer deptId) {
        return dao.search(compCode, deptId);
    }

    @Override
    public boolean delete(GradeHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(GradeHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean open(GradeHisKey key) {
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
    public GradeHisDetail save(GradeHisDetail b) {
        return gdDao.save(b);
    }

    @Override
    public void delete(GradeHisDetailKey key) {
        gdDao.delete(key);
    }

    @Override
    public List<GradeHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        return gdDao.search(vouNo, compCode, deptId);
    }
}
