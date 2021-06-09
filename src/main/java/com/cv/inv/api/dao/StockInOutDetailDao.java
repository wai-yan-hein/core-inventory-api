/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockInOutDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockInOutDetailDao {

    public StockInOutDetail save(StockInOutDetail stock);

    public List<StockInOutDetail> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark);

    public int delete(Integer id);

    public List<StockInOutDetail> search(String batchCode);

}
