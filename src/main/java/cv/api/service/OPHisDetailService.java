package cv.api.service;

import cv.api.entity.OPHisDetail;

import java.util.List;

public interface OPHisDetailService {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);
}
