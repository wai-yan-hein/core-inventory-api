package com.cv.inv.api.service;

import com.cv.inv.api.entity.OPHisDetail;

import java.util.List;

public interface OPHisDetailService {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo);
}
