/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.StockBrand;
import cv.api.entity.StockBrandKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockBrandService {

    StockBrand save(StockBrand brand);

    List<StockBrand> findAll(String compCode, Integer deptId);

    int delete(String id);

    StockBrand findByCode(StockBrandKey code);

    List<StockBrand> unUpload();

    Date getMaxDate();

    List<StockBrand> getBrand(String updatedDate);
}
