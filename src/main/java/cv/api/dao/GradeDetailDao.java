package cv.api.dao;

import cv.api.entity.GradeDetail;
import cv.api.entity.GradeDetailKey;

import java.time.LocalDateTime;
import java.util.List;

public interface GradeDetailDao {
    GradeDetail save(GradeDetail s);

    boolean delete(GradeDetailKey key);
    List<GradeDetail> getGradeDetail(String formulaCode,String criteriaCode, String compCode);
    List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode);
    List<GradeDetail> getGradeDetail(LocalDateTime updatedDate);

}
