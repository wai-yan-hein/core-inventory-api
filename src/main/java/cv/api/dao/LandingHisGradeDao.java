package cv.api.dao;

import cv.api.entity.*;

import java.util.List;

public interface LandingHisGradeDao {
    LandingHisGrade save(LandingHisGrade f);

    List<LandingHisGrade> getLandingGrade(String vouNo, String compCode);

    boolean delete(LandingHisGradeKey key);

    boolean delete(String vouNo, String compCode);

}
