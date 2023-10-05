package cv.api.dao;

import cv.api.entity.GradeDetailCriteria;
import cv.api.entity.GradeDetailCriteriaKey;

import java.util.List;

public interface GradeDetailCriteriaDao {
    GradeDetailCriteria save(GradeDetailCriteria f);
    boolean delete(GradeDetailCriteriaKey key);

    List<GradeDetailCriteria> getGradeDetailCriteria(String vouNo, int uniqueId, String compCode);
}
