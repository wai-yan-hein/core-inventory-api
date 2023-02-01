package cv.api.service;

import cv.api.entity.ProcessHisDetail;
import cv.api.entity.ProcessHisDetailKey;

import java.util.List;

public interface ProcessHisDetailService {
    ProcessHisDetail save(ProcessHisDetail ph);

    ProcessHisDetail findById(ProcessHisDetailKey key);

    List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId);

    void delete(ProcessHisDetailKey key);
}
