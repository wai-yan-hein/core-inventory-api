/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.RetOutDetailDao;
import com.cv.inv.api.entity.RetOutHisDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class RetOutDetailServiceImpl implements RetOutDetailService {

    private static final Logger log = LoggerFactory.getLogger(RetOutServiceImpl.class);

    @Autowired
    private RetOutDetailDao dao;

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {
        return dao.save(pd);
    }

    @Override
    public List<RetOutHisDetail> search(String glCode) {
        return dao.search(glCode);
    }

    @Override
    public int delete(String id) throws Exception{
        return dao.delete(id);
    }

}
