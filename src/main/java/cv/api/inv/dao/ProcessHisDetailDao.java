package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHisDetail;
import cv.api.inv.entity.ProcessHisDetailKey;

import java.util.List;

public interface ProcessHisDetailDao {
    ProcessHisDetail save(ProcessHisDetail ph);

    ProcessHisDetail findById(ProcessHisDetailKey key);

    List<ProcessHisDetail> search(String vouNo, String compCode, Integer deptId);

    void delete(ProcessHisDetailKey key);
}
