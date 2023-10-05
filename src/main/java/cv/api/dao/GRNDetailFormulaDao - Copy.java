package cv.api.dao;

import cv.api.entity.GradeDetailCriteria;
import cv.api.entity.GradeDetailCriteriaKey;

import java.util.List;

public interface GRNDetailFormulaDao {
    GradeDetailCriteria save(GradeDetailCriteria f);
    boolean delete(GradeDetailCriteriaKey key);

    List<GradeDetailCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode);
}
