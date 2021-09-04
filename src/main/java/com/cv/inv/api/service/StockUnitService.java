/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockUnit;
import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface StockUnitService {

     StockUnit findByCode(String code);

     StockUnit save(StockUnit unit);

     List<StockUnit> findAll(String compCode);

     int delete(String id);
}
