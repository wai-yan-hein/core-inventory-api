/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockReceiveDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface StockReceiveDetailHisDao {

    public StockReceiveDetailHis save(StockReceiveDetailHis sdh);

    public StockReceiveDetailHis findById(Long id);

    public List<StockReceiveDetailHis> search(String saleInvId);

    public int delete(String id);
}
