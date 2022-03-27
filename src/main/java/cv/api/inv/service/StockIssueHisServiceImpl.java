/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.StockIssueDetailHisDao;
import cv.api.inv.dao.StockIssueHisDao;
import cv.api.inv.entity.StockIssueDetailHis;
import cv.api.inv.entity.StockIssueHis;
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
public class StockIssueHisServiceImpl implements StockIssueHisService {

    private static final Logger log = LoggerFactory.getLogger(StockIssueHisServiceImpl.class);

    @Autowired
    private StockIssueHisDao dao;
    @Autowired
    private StockIssueDetailHisDao detailDao;

    @Override
    public StockIssueHis save(StockIssueHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public void save(StockIssueHis sdh, List<StockIssueDetailHis> listDamageDetail, String vouStatus, List<String> delList) {
        if (vouStatus.equals("EDIT")) {
            if (delList != null) {
                for (String detailId : delList) {
                    detailDao.delete(detailId);
                }
            }
        }
        dao.save(sdh);
        for (StockIssueDetailHis dh : listDamageDetail) {
            if (dh.getIssueStock().getStockCode() != null) {
                dh.setIssueId(sdh.getIssueId());
                detailDao.save(dh);
            }
        }
    }

    @Override
    public List<StockIssueHis> search(String from, String to, String location, String remark, String vouNo) {
        return dao.search(from, to, location, remark, vouNo);
    }

    @Override
    public StockIssueHis findById(String id) {
        return dao.findById(id);
    }

    @Override
    public int delete(String vouNo) {
        return dao.delete(vouNo);
    }

}
