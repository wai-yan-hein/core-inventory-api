package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.LandingHisDao;
import cv.api.dao.LandingHisGradeDao;
import cv.api.dao.LandingHisPriceDao;
import cv.api.dao.LandingHisQtyDao;
import cv.api.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LandingServiceImpl implements LandingService {
    private final SeqTableService seqTableService;
    private final LandingHisDao dao;
    private final LandingHisPriceDao priceDao;
    private final LandingHisQtyDao qtyDao;
    private final LandingHisGradeDao gradeDao;

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
        List<LandingHisPriceKey> listDelPrice = g.getListDelPrice();
        if (listDelPrice != null) listDelPrice.forEach(priceDao::delete);
        List<LandingHisPrice> listPrice = g.getListPrice();
        saveLandingPrice(listPrice, g);

        List<LandingHisQtyKey> listDelQy = g.getListDelQty();
        if (listDelQy != null) listDelQy.forEach(qtyDao::delete);
        List<LandingHisQty> listQty = g.getListQty();
        saveLandingQty(listQty, g);

        List<LandingHisGrade> listGrade = g.getListGrade();
        saveLandingGrade(listGrade, g);

        g.setListGrade(listGrade);
        g.setListPrice(listPrice);
        g.setListQty(listQty);
        dao.save(g);
        return g;
    }

    private void saveLandingPrice(List<LandingHisPrice> list, LandingHis g) {
        for (int i = 0; i < list.size(); i++) {
            LandingHisPrice cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                LandingHisPriceKey key = new LandingHisPriceKey();
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
                        LandingHisPrice pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                priceDao.save(cSd);
            }
        }
    }

    private void saveLandingQty(List<LandingHisQty> list, LandingHis g) {
        for (int i = 0; i < list.size(); i++) {
            LandingHisQty cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                LandingHisQtyKey key = new LandingHisQtyKey();
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
                        LandingHisQty pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                qtyDao.save(cSd);
            }
        }
    }

    private void saveLandingGrade(List<LandingHisGrade> list, LandingHis g) {
        String vouNo = g.getKey().getVouNo();
        String compCode = g.getKey().getCompCode();
        gradeDao.delete(vouNo, compCode);
        for (int i = 0; i < list.size(); i++) {
            LandingHisGrade cSd = list.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                LandingHisGradeKey key = new LandingHisGradeKey();
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
                        LandingHisGrade pSd = list.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                gradeDao.save(cSd);
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
    public List<LandingHisPrice> getLandingPrice(String vouNo, String compCode) {
        return priceDao.getLandingPrice(vouNo, compCode);
    }

    @Override
    public List<LandingHisQty> getLandingQty(String vouNo, String compCode) {
        return qtyDao.getLandingQty(vouNo, compCode);
    }

    @Override
    public LandingHisGrade getLandingChooseGrade(String vouNo, String compCode) {
        List<LandingHisGrade> list = gradeDao.getLandingGrade(vouNo, compCode);
        Optional<LandingHisGrade> firstMatchingGrade = list.stream()
                .filter(LandingHisGrade::isChoose)
                .findFirst();
        return firstMatchingGrade.orElse(null);

    }

    @Override
    public List<LandingHisGrade> getLandingGrade(String vouNo, String compCode) {
        return gradeDao.getLandingGrade(vouNo, compCode);
    }

    @Override
    public List<LandingHis> getLandingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                              String userCode, String stockCode, String locCode, String compCode, Integer deptId, boolean deleted) {
        return dao.getLandingHistory(fromDate, toDate, traderCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted);
    }

    @Override
    public List<LandingHis> unUploadVoucher(LocalDateTime syncDate) {
        return dao.unUploadVoucher(syncDate);
    }


    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "Landing", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
