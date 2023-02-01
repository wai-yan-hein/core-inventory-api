package cv.api.service;

import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;

import java.util.List;

public interface GRNDetailService {

    GRNDetail save(GRNDetail b);

    void delete(GRNDetailKey key);
    List<GRNDetail> search(String vouNo, String compCode, Integer deptId);
}
