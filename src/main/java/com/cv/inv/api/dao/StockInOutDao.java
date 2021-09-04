/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockInOut;

import java.util.List;

/**
 * @author Lenovo
 */
 public interface StockInOutDao {

    StockInOut save(StockInOut saleHis) throws Exception;

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                            String vouNo, String userCode);

    StockInOut findById(String id);

    int delete(String vouNo) throws Exception;

}
