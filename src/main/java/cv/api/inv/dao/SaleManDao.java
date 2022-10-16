/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.SaleMan;
import cv.api.inv.entity.SaleManKey;

import java.util.List;

/**
 * @author wai yan
 */
 public interface SaleManDao {

     SaleMan save(SaleMan saleMan);

     List<SaleMan> findAll(String compCode,Integer deptId);

     int delete(String id);

     SaleMan findByCode(SaleManKey key);
    List<SaleMan> unUpload();

}
