/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockIssueDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface StockIssueDetailHisDao {

    public StockIssueDetailHis save(StockIssueDetailHis sdh);

    public StockIssueDetailHis findById(Long id);

    public List<StockIssueDetailHis> search(String saleInvId);

    public int delete(String id);
}
