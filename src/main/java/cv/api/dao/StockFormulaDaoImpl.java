package cv.api.dao;

import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaKey;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockFormulaDaoImpl extends AbstractDao<StockFormulaKey, StockFormula> implements StockFormulaDao {
    @Override
    public StockFormula save(StockFormula s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public StockFormula find(StockFormulaKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        StockFormula f = getByKey(key);
        if (f != null) {
            f.setDeleted(true);
            f.setUpdatedDate(LocalDateTime.now());
            update(f);
            return true;
        }
        return false;
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        String hsql = "select o from StockFormula o where o.key.compCode = '" + compCode + "' order by o.userCode,o.formulaName";
        return findHSQL(hsql);
    }
}
