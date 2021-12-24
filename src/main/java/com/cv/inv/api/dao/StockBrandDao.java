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

     StockBrand save(StockBrand brand) throws Exception;

     List<StockBrand> findAll(String compCode);

     StockBrand findByCode(String code);

     int delete(String id);
}
