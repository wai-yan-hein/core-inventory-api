package cv.api.dao;

import cv.api.entity.GradeDetail;
import cv.api.entity.GradeDetailKey;

import java.util.List;

public interface GradeDetailDao {
    GradeDetail save(GradeDetail s);

    boolean delete(GradeDetailKey key);
    List<GradeDetail> getGradeDetail(String formulaCode,String criteriaCode, String compCode);
    List<GradeDetail> getCriteriaByFormula(String formulaCode, String compCode);


}