/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.SaleMan;
import cv.api.entity.SaleManKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface SaleManService {

    SaleMan save(SaleMan saleMan);

    List<SaleMan> findAll(String compCode, Integer deptId);

    int delete(String id);

    SaleMan findByCode(SaleManKey key);

    List<SaleMan> unUpload();

    List<SaleMan> getSaleMan(LocalDateTime updatedDate);

}
