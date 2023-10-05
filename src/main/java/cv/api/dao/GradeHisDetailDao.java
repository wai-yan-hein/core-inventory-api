package cv.api.dao;

import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import cv.api.entity.GradeHisDetail;
import cv.api.entity.GradeHisDetailKey;

import java.util.List;

public interface GradeHisDetailDao {

    GradeHisDetail save(GradeHisDetail b);

    void delete(GradeHisDetailKey key);
    List<GradeHisDetail> search(String vouNo, String compCode, Integer deptId);

}
