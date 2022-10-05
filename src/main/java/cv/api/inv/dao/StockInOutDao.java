/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockIOKey;
import cv.api.inv.entity.StockInOut;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockInOutDao {

    StockInOut save(StockInOut saleHis) throws Exception;

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                            String vouNo, String userCode,String vouStatus);

    StockInOut findById(StockIOKey id);

    int delete(String vouNo) throws Exception;

}
