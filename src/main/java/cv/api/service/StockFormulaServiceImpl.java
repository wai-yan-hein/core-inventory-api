package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.StockFormulaDao;
import cv.api.dao.StockFormulaDetailDao;
import cv.api.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {
    private final StockFormulaDao formulaDao;
    private final StockFormulaDetailDao formulaDetailDao;
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
    public StockFormulaDetail save(StockFormulaDetail s) {
        return formulaDetailDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaDetailKey key) {
        return formulaDetailDao.delete(key);
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code, String compCode) {
        return formulaDetailDao.getFormulaDetail(code, compCode);
    }
}
