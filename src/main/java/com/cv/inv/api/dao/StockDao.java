/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Stock;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockDao {

    public Stock save(Stock stock);

    public Stock findById(String id);

    public List<Stock> findAll();

    public int delete(String id);

    public Object getMax(String sql);

    public List<Stock> findActiveStock();

    public List<Stock> search(String stockType);

    public List<Stock> searchC(String stockType);

    public List<Stock> searchB(String stockType);
    
    public List<Stock> searchM(String updatedDate);

}
