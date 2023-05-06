/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.RetInDetailDao;
import cv.api.entity.RetInHisDetail;
import cv.api.entity.RetInKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class RetInDetailServiceImpl implements RetInDetailService {

    @Autowired
    private RetInDetailDao dao;

    @Override
    public RetInHisDetail save(RetInHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<RetInHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(RetInKey key) {
        return dao.delete(key);
    }

}
