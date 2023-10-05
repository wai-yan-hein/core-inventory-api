package cv.api.dao;

import cv.api.entity.GradeDetailCriteria;
import cv.api.entity.GradeDetailCriteriaKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GRNDetailFormulaDaoImpl extends AbstractDao<GradeDetailCriteriaKey, GradeDetailCriteria> implements GRNDetailFormulaDao {
    @Override
    public GradeDetailCriteria save(GradeDetailCriteria f) {
        return f;
    }

    @Override
    public boolean delete(GradeDetailCriteriaKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<GradeDetailCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GRNDetailFormula o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
