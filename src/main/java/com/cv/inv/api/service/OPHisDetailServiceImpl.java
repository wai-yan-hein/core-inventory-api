package com.cv.inv.api.service;

import com.cv.inv.api.dao.OPHisDetailDao;
import com.cv.inv.api.entity.OPHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OPHisDetailServiceImpl implements OPHisDetailService {
    @Autowired
    private OPHisDetailDao dao;

    @Override
    public OPHisDetail save(OPHisDetail op) {
        return dao.save(op);
    }

    @Override
    public List<OPHisDetail> search(String vouNo) {
        return dao.search(vouNo);
    }
}
