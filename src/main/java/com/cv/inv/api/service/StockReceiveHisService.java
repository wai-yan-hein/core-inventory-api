/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockReceiveDetailHis;
import com.cv.inv.api.entity.StockReceiveHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
 public interface StockReceiveHisService {

     StockReceiveHis save(StockReceiveHis sdh);

     List<StockReceiveHis> search(String from, String to, String location,
            String remark, String vouNo);

     void save(StockReceiveHis sdh, List<StockReceiveDetailHis> listDamageDetail, String vouStatus, List<String> delList);

     StockReceiveHis findById(String id);

     int delete(String vouNo);
}
