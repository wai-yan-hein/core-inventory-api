package cv.api.inv.service;

import cv.api.inv.entity.OPHis;

import java.util.List;

public interface OPHisService {
    OPHis save(OPHis op);

    OPHis findByCode(String vouNo);

    List<OPHis> search(String compCode);
}
