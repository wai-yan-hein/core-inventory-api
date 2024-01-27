/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.StockType;
import cv.api.entity.StockTypeKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockTypeDao {

    StockType save(StockType item);

    List<StockType> findAll(String compCode, Integer deptId);

    int delete(String id);

    StockType findByCode(StockTypeKey key);

    List<StockType> unUpload();

    List<StockType> getStockType(LocalDateTime updatedDate);
}
