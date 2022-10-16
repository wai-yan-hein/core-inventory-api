/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.RetOutDetailDao;
import cv.api.inv.entity.RetOutHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class RetOutDetailServiceImpl implements RetOutDetailService {


    @Autowired
    private RetOutDetailDao dao;

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<RetOutHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo,compCode,deptId);
    }

    @Override
    public int delete(String id) throws Exception{
        return dao.delete(id);
    }

}
