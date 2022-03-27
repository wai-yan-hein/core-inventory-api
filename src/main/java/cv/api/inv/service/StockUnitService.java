/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockUnit;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockUnitService {

     StockUnit findByCode(String code);

     StockUnit save(StockUnit unit) throws Exception;

     List<StockUnit> findAll(String compCode);

     int delete(String id);
}
