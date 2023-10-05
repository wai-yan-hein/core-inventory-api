package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.GRNDao;
import cv.api.dao.GRNDetailDao;
import cv.api.dao.GRNDetailFormulaDao;
import cv.api.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GRNServiceImpl implements GRNService {
    private final SeqTableService seqTableService;
    private final GRNDao dao;
    private final GRNDetailDao gdDao;

    @Override
    public GRN findByCode(GRNKey key) {
        return dao.findByCode(key);
    }

    @Override
    public GRN save(GRN g) {
        g.setVouDate(Util1.toDateTime(g.getVouDate()));
        if (Util1.isNullOrEmpty(g.getKey().getVouNo())) {
            g.getKey().setVouNo(getVoucherNo(g.getDeptId(), g.getMacId(), g.getKey().getCompCode()));
        }
        List<GRNDetail> listDetail = g.getListDetail();
        List<GRNDetailKey> listDel = g.getListDel();
        //backup
        if (listDel != null) {
            listDel.forEach(gdDao::delete);
        }

        saveGRNDetail(listDetail, g);
        g.setListDetail(listDetail);
        dao.save(g);
        return g;
    }

    private void saveGRNDetail(List<GRNDetail> list, GRN g) {
        for (int i = 0; i < list.size(); i++) {
            GRNDetail cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                GRNDetailKey key = new GRNDetailKey();
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
                        GRNDetail pSd = list.get(i - 1);
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
    public List<GRN> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<GRN> search(String batchNo, String compCode, Integer deptId) {
        return dao.search(batchNo, compCode, deptId);
    }

    @Override
    public boolean delete(GRNKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(GRNKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean open(GRNKey key) {
        return dao.open(key);
    }

    @Override
    public GRN findByBatchNo(String batchNo, String compCode, Integer deptId) {
        return dao.findByBatchNo(batchNo, compCode, deptId);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "GRN", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
