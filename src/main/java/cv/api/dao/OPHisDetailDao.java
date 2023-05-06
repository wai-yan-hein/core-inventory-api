package cv.api.dao;

import cv.api.entity.OPHisDetail;
import cv.api.entity.OPHisDetailKey;

import java.util.List;

public interface OPHisDetailDao {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(OPHisDetailKey key);


}
