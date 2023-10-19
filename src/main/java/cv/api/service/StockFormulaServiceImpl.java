package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.*;
import cv.api.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {
    private final StockFormulaDao formulaDao;
    private final StockFormulaPriceDao formulaPriceDao;
    private final StockFormulaQtyDao formulaQtyDao;
    private final GradeDetailDao gradeDetailDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public StockFormula save(StockFormula s) {
        if (Util1.isNullOrEmpty(s.getKey().getFormulaCode())) {
            s.getKey().setFormulaCode(getCode(s.getKey().getCompCode()));
            s.setCreatedDate(Util1.getTodayLocalDate());
        } else {
            s.setUpdatedDate(Util1.getTodayLocalDate());
        }
        formulaDao.save(s);
        return s;
    }

    @Override
    public StockFormula find(StockFormulaKey key) {
        return formulaDao.find(key);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockFormula", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        return formulaDao.delete(key);
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        return formulaDao.getFormula(compCode);
    }

    @Override
    public StockFormulaPrice save(StockFormulaPrice s) {
        return formulaPriceDao.save(s);
    }

    @Override
    public StockFormulaQty save(StockFormulaQty s) {
        return formulaQtyDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaPriceKey key) {
        return formulaPriceDao.delete(key);
    }

    @Override
    public boolean delete(StockFormulaQtyKey key) {
        return formulaQtyDao.delete(key);
    }

    @Override
    public boolean delete(GradeDetailKey key) {
        return gradeDetailDao.delete(key);
    }

    @Override
    public List<StockFormulaPrice> getFormulaPrice(String code, String compCode) {
        return formulaPriceDao.getFormulaDetail(code, compCode);
    }

    @Override
    public List<StockFormulaQty> getFormulaQty(String code, String compCode) {
        return formulaQtyDao.getFormulaDetail(code, compCode);
    }

    @Override
    public List<StockFormula> getStockFormula(LocalDateTime updatedDate) {
        return formulaDao.getStockFormula(updatedDate);
    }

    @Override
    public GradeDetail save(GradeDetail g) {
        return gradeDetailDao.save(g);
    }

    @Override
    public List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode) {
        return gradeDetailDao.getGradeDetail(formulaCode, criteriaCode, compCode);
    }

    @Override
    public List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode) {
        return gradeDetailDao.getStockFormulaGrade(formulaCode, compCode);
    }
}
