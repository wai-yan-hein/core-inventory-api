/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Stock;
import cv.api.inv.entity.StockKey;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockService {

     Stock save(Stock stock) throws Exception;

    Stock findById(StockKey key);

     List<Stock> findAll(String compCode);

    List<String> delete(StockKey key);

     List<Stock> findActiveStock(String compCode);

    List<Stock> search(String stockCode, String stockType, String cat, String brand);

    List<Stock> getStock(String str, String compCode);

}
