/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockBrand;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockBrandDao {

    public StockBrand save(StockBrand brand) throws Exception;

    public List<StockBrand> findAll(String compCode);

    public StockBrand findByCode(String code);

    public int delete(String id);
}
