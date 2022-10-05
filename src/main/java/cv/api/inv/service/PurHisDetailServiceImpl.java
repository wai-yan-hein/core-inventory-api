/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.PurHisDetailDao;
import cv.api.inv.entity.PurDetailKey;
import cv.api.inv.entity.PurHis;
import cv.api.inv.entity.PurHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
public class PurHisDetailServiceImpl implements PurHisDetailService {

    @Autowired
    private PurHisDetailDao dao;

    @Override
    public PurHisDetail save(PurHisDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<PurHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo,compCode,deptId);
    }

    @Override
    public int delete(String code) throws Exception {
        return dao.delete(code);

    }
}
