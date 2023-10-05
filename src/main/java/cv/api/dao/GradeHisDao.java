package cv.api.dao;

import cv.api.entity.GRN;
import cv.api.entity.GRNKey;
import cv.api.entity.GradeHis;
import cv.api.entity.GradeHisKey;

import java.util.List;

public interface GradeHisDao {
    GradeHis findByCode(GradeHisKey key);

    GradeHis save(GradeHis b);

    List<GradeHis> findAll(String compCode, Integer deptId);

    boolean delete(GradeHisKey key);
    boolean restore(GradeHisKey key);

    List<GradeHis> search(String compCode, Integer deptId);
    boolean open(GradeHisKey key);


}
