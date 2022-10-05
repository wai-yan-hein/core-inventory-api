/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.StockInOutDetailDao;
import cv.api.inv.entity.StockInOutDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockInOutDetailServiceImpl implements StockInOutDetailService {

    @Autowired
    private StockInOutDetailDao dao;

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        return dao.save(stock);
    }

    @Override
    public int delete(String code) {
        return dao.delete(code);
    }

    @Override
    public List<StockInOutDetail> search(String vouNo,String compCode,Integer deptId) {
        return dao.search(vouNo,compCode,deptId);
    }

}
