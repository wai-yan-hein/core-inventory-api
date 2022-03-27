package cv.api.inv.dao;

import cv.api.inv.entity.OPHis;

import java.util.List;

public interface OPHisDao {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(String vouNo);

    List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode);

}
