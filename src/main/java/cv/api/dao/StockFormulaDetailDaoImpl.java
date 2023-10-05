package cv.api.dao;

import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaDetail;
import cv.api.entity.StockFormulaDetailKey;
import cv.api.entity.StockFormulaKey;
import cv.api.service.StockFormulaService;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockFormulaDetailDaoImpl extends AbstractDao<StockFormulaDetailKey, StockFormulaDetail> implements StockFormulaDetailDao {
    @Override
    public StockFormulaDetail save(StockFormulaDetail s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public boolean delete(StockFormulaDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code, String compCode) {
        String hsql = "select o from StockFormulaDetail o where o.key.compCode = '" + compCode + "' and o.key.code ='" + code + "'";
        return findHSQL(hsql);
    }
}
