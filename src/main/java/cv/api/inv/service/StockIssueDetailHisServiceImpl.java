/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.StockIssueDetailHisDao;
import cv.api.inv.entity.StockIssueDetailHis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockIssueDetailHisServiceImpl implements StockIssueDetailHisService {

    @Autowired
    private StockIssueDetailHisDao dao;

    @Override
    public List<StockIssueDetailHis> search(String dmgVouId) {
        return dao.search(dmgVouId);
    }

}
