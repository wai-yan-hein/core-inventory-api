/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutDetailService {

    StockInOutDetail save(StockInOutDetail stock);

    List<StockInOutDetail> search(String vouNo, String compCode);

    List<StockInOutDetail> searchByJob(String jobId, String compCode);

    int delete(StockInOutKey key);
}
