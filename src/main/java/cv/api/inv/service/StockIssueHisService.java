/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockIssueDetailHis;
import cv.api.inv.entity.StockIssueHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockIssueHisService {

     StockIssueHis save(StockIssueHis sdh);

     List<StockIssueHis> search(String from, String to, String location,
            String remark, String vouNo);

     void save(StockIssueHis sdh, List<StockIssueDetailHis> listDamageDetail, String vouStatus, List<String> delList);

     StockIssueHis findById(String id);

     int delete(String vouNo);
}
