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

     StockIssueDetailHis save(StockIssueDetailHis sdh);

     StockIssueDetailHis findById(Long id);

     List<StockIssueDetailHis> search(String saleInvId);

     int delete(String id);
}
