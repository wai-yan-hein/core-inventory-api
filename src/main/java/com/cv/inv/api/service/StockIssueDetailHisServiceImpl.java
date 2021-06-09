/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.StockIssueDetailHisDao;
import com.cv.inv.api.entity.StockIssueDetailHis;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lenovo
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
