/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Stock;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockService {

    public Stock save(Stock stock) throws Exception;

    public Stock findById(String id);

    public List<Stock> findAll(String compCode);

    public int delete(String id);

    public List<Stock> findActiveStock();

    public List<Stock> search(String stockType);

    public List<Stock> searchC(String stockCat);

    public List<Stock> searchB(String stockBrand);

    public List<Stock> searchM(String updatedDate);

}
