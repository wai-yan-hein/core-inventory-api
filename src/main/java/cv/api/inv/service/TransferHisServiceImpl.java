/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.TransferDetailHisDao;
import cv.api.inv.dao.TransferHisDao;
import cv.api.inv.entity.TransferDetailHis;
import cv.api.inv.entity.TransferHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class TransferHisServiceImpl implements TransferHisService {

    private static final Logger log = LoggerFactory.getLogger(TransferHisServiceImpl.class);

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
