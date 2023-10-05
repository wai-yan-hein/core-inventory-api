package cv.api.service;

import cv.api.entity.*;

import java.util.List;

public interface GradeService {
    GradeHis findByCode(GradeHisKey key);

    GradeHis save(GradeHis b);

    List<GradeHis> findAll(String compCode, Integer deptId);

    List<GradeHis> search(String compCode, Integer deptId);

    boolean delete(GradeHisKey key);
    boolean restore(GradeHisKey key);

    boolean open(GradeHisKey key);

    // grade detail
    GradeHisDetail save(GradeHisDetail b);

    void delete(GradeHisDetailKey key);
    List<GradeHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);
}
