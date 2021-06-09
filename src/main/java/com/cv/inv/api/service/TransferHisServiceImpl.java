/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.TransferDetailHisDao;
import com.cv.inv.api.dao.TransferHisDao;
import com.cv.inv.api.entity.TransferDetailHis;
import com.cv.inv.api.entity.TransferHis;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class TransferHisServiceImpl implements TransferHisService {

    private static final Logger logger = LoggerFactory.getLogger(TransferHisServiceImpl.class);

    @Autowired
    private TransferHisDao dao;
    @Autowired
    private TransferDetailHisDao detailDao;

    @Override
    public TransferHis save(TransferHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public void save(TransferHis sdh, List<TransferDetailHis> listTransferDetail, String vouStatus, List<String> delList) {
        if (vouStatus.equals("EDIT")) {
            if (delList != null) {
                for (String detailId : delList) {
                    detailDao.delete(detailId);
                }
            }
        }
        dao.save(sdh);
        for (TransferDetailHis dh : listTransferDetail) {
            if (dh.getMedicineId().getStockCode() != null) {
                dh.setTranVouId(sdh.getTranVouId());
                detailDao.save(dh);
            }
        }
    }

    @Override
    public List<TransferHis> search(String from, String to, String location, String remark, String vouNo) {
        return dao.search(from, to, location, remark, vouNo);
    }

    @Override
    public TransferHis findById(String id) {
        return dao.findById(id);
    }

    @Override
    public int delete(String vouNo) {
        return dao.delete(vouNo);
    }

}
