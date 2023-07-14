/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.MilingRawDao;
import cv.api.dao.SaleHisDetailDao;
import cv.api.entity.MilingRawDetail;
import cv.api.entity.MilingRawDetailKey;
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
public class MilingRawServiceImpl implements MilingRawService {

    @Autowired
    private MilingRawDao dao;

    @Override
    public MilingRawDetail save(MilingRawDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<MilingRawDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(MilingRawDetailKey key) {
        return dao.delete(key);
    }

    @Override
    public List<MilingRawDetail> getSaleByBatch(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatch(batchNo, compCode, depId);
    }

    @Override
    public List<MilingRawDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId) {
        return dao.getSaleByBatchDetail(batchNo,compCode,depId);
    }
}
