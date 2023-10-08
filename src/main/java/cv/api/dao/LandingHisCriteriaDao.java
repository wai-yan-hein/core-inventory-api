package cv.api.dao;

import cv.api.entity.LandingHisCriteria;
import cv.api.entity.LandingHisCriteriaKey;

import java.util.List;

public interface LandingHisCriteriaDao {
    LandingHisCriteria save(LandingHisCriteria f);
    boolean delete(LandingHisCriteriaKey key);

    List<LandingHisCriteria> getLandingDetailCriteria(String vouNo,String compCode);
}
