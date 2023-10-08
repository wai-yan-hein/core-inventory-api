package cv.api.dao;

import cv.api.entity.LandingHisCriteria;
import cv.api.entity.LandingHisCriteriaKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GRNDetailFormulaDaoImpl extends AbstractDao<LandingHisCriteriaKey, LandingHisCriteria> implements GRNDetailFormulaDao {
    @Override
    public LandingHisCriteria save(LandingHisCriteria f) {
        return f;
    }

    @Override
    public boolean delete(LandingHisCriteriaKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<LandingHisCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GRNDetailFormula o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
