/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.StockType;
import cv.api.entity.StockTypeKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockTypeService {

    StockType findByCode(StockTypeKey key);

    StockType save(StockType item);

    List<StockType> findAll(String compCode, Integer deptId);

    int delete(String id);

    List<StockType> unUpload();

    Date getMaxDate();

    List<StockType> getStockType(String updatedDate);
}
