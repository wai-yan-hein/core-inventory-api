/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockUnit;
import cv.api.inv.entity.StockUnitKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
 public interface StockUnitDao {

     StockUnit save(StockUnit unit);

     List<StockUnit> findAll(String compCode,Integer deptId);

     int delete(String id);

     StockUnit findByCode(StockUnitKey code);
    List<StockUnit> unUpload();
    Date getMaxDate();

}
