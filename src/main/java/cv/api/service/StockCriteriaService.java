/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockCriteriaService {
    List<StockCriteria> getCriteria(LocalDateTime updatedDate);

    StockCriteria findByCode(StockCriteriaKey key);

    StockCriteria save(StockCriteria category);

    List<StockCriteria> findAll(String compCode, boolean active);

    int delete(String id);

    List<StockCriteria> search(String text,String compCode);
    List<StockCriteria> unUpload();

    LocalDateTime getMaxDate();

}
