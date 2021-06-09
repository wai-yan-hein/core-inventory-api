/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockBrand;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockBrandService {

    public StockBrand save(StockBrand brand);

    public List<StockBrand> findAll();

    public int delete(String id);
}
