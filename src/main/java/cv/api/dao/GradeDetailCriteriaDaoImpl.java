package cv.api.dao;

import cv.api.entity.GradeDetailCriteria;
import cv.api.entity.GradeDetailCriteriaKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GradeDetailCriteriaDaoImpl extends AbstractDao<GradeDetailCriteriaKey, GradeDetailCriteria> implements GradeDetailCriteriaDao {
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
    public List<GradeDetailCriteria> getGradeDetailCriteria(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GradeDetailCriteria o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
