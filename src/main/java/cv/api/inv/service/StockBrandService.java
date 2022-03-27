/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockBrand;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockBrandService {

     StockBrand save(StockBrand brand)throws Exception;

     List<StockBrand> findAll(String compCode);

     int delete(String id);

     StockBrand findByCode(String code);

}
