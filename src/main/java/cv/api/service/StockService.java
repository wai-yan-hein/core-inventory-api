/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.Stock;
import cv.api.entity.StockKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockService {

    Stock save(Stock stock);

    Stock findById(StockKey key);

    List<Stock> findAll(String compCode, Integer deptId);

    List<String> delete(StockKey key);

    List<Stock> findActiveStock(String compCode, Integer deptId);

    List<Stock> search(String stockCode, String stockType, String cat, String brand, String compCode, Integer deptId,boolean orderFavorite);

    List<Stock> getStock(String str, String compCode, Integer deptId);

    List<Stock> getService(String compCode, Integer deptId);

    List<Stock> unUpload();

    Date getMaxDate();

    List<Stock> getStock(String updatedDate);

    void update(StockKey key, boolean favorite);
}
