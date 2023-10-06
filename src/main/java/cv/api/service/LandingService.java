package cv.api.service;

import cv.api.entity.*;

import java.util.List;

public interface LandingService {
    LandingHis findByCode(LandingHisKey key);

    LandingHis save(LandingHis b);

    List<LandingHis> findAll(String compCode, Integer deptId);

    List<LandingHis> search(String compCode, Integer deptId);

    boolean delete(LandingHisKey key);
    boolean restore(LandingHisKey key);

    boolean open(LandingHisKey key);

    // grade detail
    LandingHisDetail save(LandingHisDetail b);

    void delete(LandingHisDetailKey key);
    List<LandingHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);
}
