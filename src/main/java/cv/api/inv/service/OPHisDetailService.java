package cv.api.inv.service;

import cv.api.inv.entity.OPHisDetail;

import java.util.List;

public interface OPHisDetailService {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo);
}
