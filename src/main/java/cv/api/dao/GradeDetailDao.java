package cv.api.dao;

import cv.api.entity.GradeDetail;
import cv.api.entity.GradeDetailKey;
import cv.api.entity.StockFormulaDetail;
import cv.api.entity.StockFormulaDetailKey;

import java.util.List;

public interface GradeDetailDao {
    GradeDetail save(GradeDetail s);

    boolean delete(GradeDetailKey key);
    List<GradeDetail> getGradeDetail(String formulaCode,String criteriaCode, String compCode);
    List<GradeDetail> getCriteriaByFormula(String formulaCode, String compCode);


}
