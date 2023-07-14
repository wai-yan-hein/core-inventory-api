/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.MilingOutDao;
import cv.api.dao.SaleHisDetailDao;
import cv.api.entity.MilingOutDetail;
import cv.api.entity.MilingOutDetailKey;
import cv.api.entity.SaleDetailKey;
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
public class MilingOutServiceImpl implements MilingOutService {

    @Autowired
    private MilingOutDao dao;

    @Override
    public MilingOutDetail save(MilingOutDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<MilingOutDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(MilingOutDetailKey key) {
        return dao.delete(key);
    }

    @Override
    public List<MilingOutDetail> getSaleByBatch(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatch(batchNo, compCode, depId);
    }

    @Override
    public List<MilingOutDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatchDetail(batchNo,compCode,depId);
    }
}
