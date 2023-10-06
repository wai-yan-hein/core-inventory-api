/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockCriteriaDao {
    StockCriteria findByCode(StockCriteriaKey key);

    StockCriteria save(StockCriteria item);

    List<StockCriteria> findAll(String compCode, boolean active);

    List<StockCriteria> search(String compCode, String text);

    List<StockCriteria> unUpload();

    int delete(String id);

    LocalDateTime getMaxDate();

    List<StockCriteria> getCriteria(LocalDateTime updatedDate);


    List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite);



}
