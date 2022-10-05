/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockType;
import cv.api.inv.entity.StockTypeKey;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockTypeDao {

     StockType save(StockType item);

     List<StockType> findAll(String compCode, Integer deptId);

     int delete(String id);

    StockType findByCode(StockTypeKey key);
}
