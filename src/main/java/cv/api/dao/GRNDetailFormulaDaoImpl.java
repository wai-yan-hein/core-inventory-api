package cv.api.dao;

import cv.api.entity.LandingDetailCriteria;
import cv.api.entity.LandingDetailCriteriaKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GRNDetailFormulaDaoImpl extends AbstractDao<LandingDetailCriteriaKey, LandingDetailCriteria> implements GRNDetailFormulaDao {
    @Override
    public LandingDetailCriteria save(LandingDetailCriteria f) {
        return f;
    }

    @Override
    public boolean delete(LandingDetailCriteriaKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<LandingDetailCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GRNDetailFormula o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
