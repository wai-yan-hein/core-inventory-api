/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockIssueHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockIssueHisDao {

     StockIssueHis save(StockIssueHis ph);

     StockIssueHis findById(String id);

     List<StockIssueHis> search(String from, String to, String location,
            String remark, String vouNo);
    
    

     int delete(String vouNo);
}
