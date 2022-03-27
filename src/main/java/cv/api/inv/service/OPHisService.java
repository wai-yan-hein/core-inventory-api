package cv.api.inv.service;

import cv.api.inv.entity.OPHis;

import java.util.List;

public interface OPHisService {
    OPHis save(OPHis op);

    List<OPHis> search(String compCode);
    
    List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode);
}
