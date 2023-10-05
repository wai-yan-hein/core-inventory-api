package cv.api.service;

import cv.api.dao.StockFormulaDao;
import cv.api.dao.StockFormulaDetailDao;
import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaDetail;
import cv.api.entity.StockFormulaDetailKey;
import cv.api.entity.StockFormulaKey;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {
    private final StockFormulaDao formulaDao;
    private final StockFormulaDetailDao formulaDetailDao;

    @Override
    public StockFormula save(StockFormula s) {
        return formulaDao.save(s);
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
