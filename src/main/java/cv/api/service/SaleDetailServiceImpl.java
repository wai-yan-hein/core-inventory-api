/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.SaleHisDao;
import cv.api.dao.SaleHisDetailDao;
import cv.api.entity.SaleHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    @Autowired
    private SaleHisDetailDao dao;

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(String code, String compCode, Integer deptId) {
        return dao.delete(code, compCode, deptId);
    }

    @Override
    public List<SaleHisDetail> getSaleByBatch(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatch(batchNo, compCode, depId);
    }

    @Override
    public List<SaleHisDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatchDetail(batchNo,compCode,depId);
    }
}
