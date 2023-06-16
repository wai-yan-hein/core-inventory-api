/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.StockBrand;
import cv.api.entity.StockBrandKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockBrandDao {

    StockBrand save(StockBrand brand);

    List<StockBrand> findAll(String compCode, Integer deptId);

    StockBrand findByCode(StockBrandKey code);

    int delete(String id);

    List<StockBrand> unUpload();

    Date getMaxDate();

    List<StockBrand> getBrand(LocalDateTime updatedDate);


}
