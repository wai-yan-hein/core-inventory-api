/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockType;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface StockTypeService {

    public StockType findByCode(String code);

    public StockType save(StockType item) throws Exception;

    public List<StockType> findAll(String compCode);

    public int delete(String id);
}
