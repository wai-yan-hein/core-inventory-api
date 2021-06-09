/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockReceiveHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface StockReceiveHisDao {

    public StockReceiveHis save(StockReceiveHis ph);

    public StockReceiveHis findById(String id);

    public List<StockReceiveHis> search(String from, String to, String location,
            String remark, String vouNo);

    public int delete(String vouNo);
}
