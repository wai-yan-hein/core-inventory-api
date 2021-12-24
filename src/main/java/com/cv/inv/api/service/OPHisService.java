package com.cv.inv.api.service;

import com.cv.inv.api.entity.OPHis;
import com.cv.inv.api.entity.OPHisDetail;

import java.util.List;

public interface OPHisService {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);
    
    List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode);
}
