package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OPHis;

import java.util.List;

public interface OPHisDao {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);

    OPHis findByCode(String vouNo);

    List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode);

}
