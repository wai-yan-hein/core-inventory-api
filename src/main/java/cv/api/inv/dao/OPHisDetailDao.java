package cv.api.inv.dao;

import cv.api.inv.entity.OPHisDetail;

import java.util.List;

public interface OPHisDetailDao {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(String opCode, String compCode, Integer deptId);


}
