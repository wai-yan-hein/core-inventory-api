package cv.api.dao;

import cv.api.entity.LandingHisCriteria;
import cv.api.entity.LandingHisCriteriaKey;

import java.util.List;

public interface GRNDetailFormulaDao {
    LandingHisCriteria save(LandingHisCriteria f);
    boolean delete(LandingHisCriteriaKey key);

    List<LandingHisCriteria> getGRNDetailFormula(String vouNo, int uniqueId, String compCode);
}
