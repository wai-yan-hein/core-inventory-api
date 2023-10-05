package cv.api.dao;

import cv.api.entity.GRNDetailFormula;
import cv.api.entity.GRNDetailFormulaKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GRNDetailFormulaDaoImpl extends AbstractDao<GRNDetailFormulaKey, GRNDetailFormula> implements GRNDetailFormulaDao {
    @Override
    public GRNDetailFormula save(GRNDetailFormula f) {
        return f;
    }

    @Override
    public boolean delete(GRNDetailFormulaKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<GRNDetailFormula> getGRNDetailFormula(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GRNDetailFormula o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
