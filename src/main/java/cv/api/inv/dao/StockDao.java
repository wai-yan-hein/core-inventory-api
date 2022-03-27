/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Stock;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockDao {

     Stock save(Stock stock);

     Stock findById(String id);

     List<Stock> findAll(String compCode);

     int delete(String id);

     List<Stock> findActiveStock(String compCode);

     List<Stock> search(String stockType);

     List<Stock> searchC(String stockType);

     List<Stock> searchB(String stockType);

     List<Stock> searchM(String updatedDate);

}
