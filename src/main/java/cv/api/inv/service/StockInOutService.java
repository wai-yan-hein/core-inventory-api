/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockInOut;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockInOutService {

    StockInOut save(StockInOut io) throws Exception;

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                            String vouNo, String userCode,String vouStatus);

    StockInOut findById(String id);

    int delete(String vouNo) throws Exception;
}
