/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockIssueDetailHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockIssueDetailHisDao {

     StockIssueDetailHis save(StockIssueDetailHis sdh);

     StockIssueDetailHis findById(Long id);

     List<StockIssueDetailHis> search(String saleInvId);

     int delete(String id);
}
