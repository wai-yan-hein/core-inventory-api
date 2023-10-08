package cv.api.dao;

import cv.api.entity.LandingHis;
import cv.api.entity.LandingHisKey;

import java.util.List;

public interface LandingHisDao {
    LandingHis findByCode(LandingHisKey key);

    LandingHis save(LandingHis b);

    List<LandingHis> findAll(String compCode, Integer deptId);

    boolean delete(LandingHisKey key);
    boolean restore(LandingHisKey key);
    List<LandingHis> getLandingHistory(String fromDate, String toDate,String traderCode, String vouNo,
                                       String remark, String userCode, String stockCode, String locCode,
                                       String compCode, Integer deptId, boolean deleted);



}
