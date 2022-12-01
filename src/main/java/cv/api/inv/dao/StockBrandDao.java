/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockBrand;
import cv.api.inv.entity.StockBrandKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockBrandDao {

    StockBrand save(StockBrand brand) throws Exception;

    List<StockBrand> findAll(String compCode, Integer deptId);

    StockBrand findByCode(StockBrandKey code);

    int delete(String id);

    List<StockBrand> unUpload();

    Date getMaxDate();
    List<StockBrand> getBrand(String updatedDate);


}
