/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockReceiveDetailHisDao;
import com.cv.inv.api.dao.StockReceiveHisDao;
import com.cv.inv.api.entity.StockReceiveDetailHis;
import com.cv.inv.api.entity.StockReceiveHis;
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
public class StockReceiveHisServiceImpl implements StockReceiveHisService {

    private static final Logger log = LoggerFactory.getLogger(StockReceiveHisServiceImpl.class);

    @Autowired
    private StockReceiveHisDao dao;
    @Autowired
    private StockReceiveDetailHisDao detailDao;

    @Override
    public StockReceiveHis save(StockReceiveHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public void save(StockReceiveHis sdh, List<StockReceiveDetailHis> listDamageDetail, String vouStatus, List<String> delList) {
        if (vouStatus.equals("EDIT")) {
            if (delList != null) {
                for (String detailId : delList) {
                    detailDao.delete(detailId);
                }
            }
        }
        dao.save(sdh);
        for (StockReceiveDetailHis dh : listDamageDetail) {
            if (dh.getRecMed().getStockCode() != null) {
                dh.setRefVou(sdh.getReceivedId());
                detailDao.save(dh);
            }
        }
    }

    @Override
    public List<StockReceiveHis> search(String from, String to, String location, String remark, String vouNo) {
        return dao.search(from, to, location, remark, vouNo);
    }

    @Override
    public StockReceiveHis findById(String id) {
        return dao.findById(id);
    }

    @Override
    public int delete(String vouNo) {
        return dao.delete(vouNo);
    }

}
