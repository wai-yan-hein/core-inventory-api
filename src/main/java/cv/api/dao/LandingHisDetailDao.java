package cv.api.dao;

import cv.api.entity.LandingHisDetail;
import cv.api.entity.LandingHisDetailKey;

import java.util.List;

public interface LandingHisDetailDao {

    LandingHisDetail save(LandingHisDetail b);

    void delete(LandingHisDetailKey key);
    List<LandingHisDetail> search(String vouNo, String compCode, Integer deptId);

}
