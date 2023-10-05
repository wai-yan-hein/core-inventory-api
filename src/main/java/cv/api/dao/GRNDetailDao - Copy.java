package cv.api.dao;

import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;

import java.util.List;

public interface GRNDetailDao {

    GRNDetail save(GRNDetail b);

    void delete(GRNDetailKey key);
    List<GRNDetail> search(String vouNo, String compCode, Integer deptId);

}
