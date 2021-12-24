package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OPHisDetail;

import java.util.List;

public interface OPHisDetailDao {
    OPHisDetail save(OPHisDetail op);

    List<OPHisDetail> search(String vouNo);

    int delete(String opCode);


}
