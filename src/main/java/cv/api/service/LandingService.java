package cv.api.service;

import cv.api.entity.*;

import java.util.List;

public interface LandingService {
    LandingHis findByCode(LandingHisKey key);

    LandingHis save(LandingHis b);

    List<LandingHis> findAll(String compCode, Integer deptId);

    boolean delete(LandingHisKey key);

    boolean restore(LandingHisKey key);
    List<LandingHisPrice> getLandingPrice(String vouNo, String compCode);
    List<LandingHisQty> getLandingQty(String vouNo, String compCode);
    List<LandingHisGrade> getLandingGrade(String vouNo, String compCode);

    List<LandingHis> getLandingHistory(String fromDate, String toDate,String traderCode, String vouNo,
                            String remark, String userCode, String stockCode, String locCode,
                            String compCode, Integer deptId, boolean deleted);
}
