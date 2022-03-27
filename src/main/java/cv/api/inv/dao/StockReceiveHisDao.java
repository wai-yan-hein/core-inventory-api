/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockReceiveHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockReceiveHisDao {

     StockReceiveHis save(StockReceiveHis ph);

     StockReceiveHis findById(String id);

     List<StockReceiveHis> search(String from, String to, String location,
            String remark, String vouNo);

     int delete(String vouNo);
}
