package cv.api.dao;

import cv.api.entity.LandingDetailCriteria;
import cv.api.entity.LandingDetailCriteriaKey;

import java.util.List;

public interface GRNDetailFormulaDao {
    LandingDetailCriteria save(LandingDetailCriteria f);
    boolean delete(LandingDetailCriteriaKey key);

    List<LandingDetailCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode);
}
