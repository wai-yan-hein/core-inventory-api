/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;



import com.cv.inv.api.dao.PurchaseHisDao;
import com.cv.inv.api.entity.PurHis;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class PurchaseHisServiceImpl implements PurchaseHisService {

    @Autowired
    private PurchaseHisDao hisDao;

    @Override
    public PurHis save(PurHis purHis) throws Exception {
        hisDao.save(purHis);
        return purHis;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark) {
        return hisDao.search(fromDate, toDate, cusId, vouStatusId, remark);
    }

    @Override
    public PurHis findById(String id) {
        return hisDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return hisDao.delete(vouNo);
    }

}
