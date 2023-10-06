/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.Stock;
import cv.api.entity.StockCriteria;
import cv.api.entity.StockCriteriaKey;
import cv.api.entity.StockKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockCriteriaDao {

    StockCriteria save(StockCriteria stock);

    StockCriteria findById(StockCriteriaKey key);

    List<StockCriteria> findAll(String compCode);

    int delete(StockCriteriaKey id);

    List<StockCriteria> findActiveStock(String compCode);

    List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite);

    List<StockCriteria> getStock(String str, String compCode);

    List<StockCriteria> getService(String compCode);

    List<StockCriteria> unUpload();

    Date getMaxDate();

    List<StockCriteria> getStock(LocalDateTime updatedDate);

    StockCriteria updateStock(StockCriteria stock);

}
