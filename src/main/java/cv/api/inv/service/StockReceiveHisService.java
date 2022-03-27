/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockReceiveDetailHis;
import cv.api.inv.entity.StockReceiveHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockReceiveHisService {

     StockReceiveHis save(StockReceiveHis sdh);

     List<StockReceiveHis> search(String from, String to, String location,
            String remark, String vouNo);

     void save(StockReceiveHis sdh, List<StockReceiveDetailHis> listDamageDetail, String vouStatus, List<String> delList);

     StockReceiveHis findById(String id);

     int delete(String vouNo);
}
