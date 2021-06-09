/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockInOut;
import com.cv.inv.api.entity.StockInOutDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockInOutService {

    public StockInOut findById(String id);

    public StockInOut save(StockInOut stock);

    public StockInOut save(StockInOut stock, List<StockInOutDetail> listDetail);

    public List<StockInOut> search(String batchCode, String fromDate, String toDate, String desp, String remark);

    public int delete(String id);
}
