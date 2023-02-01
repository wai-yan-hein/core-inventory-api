/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.LocationKey;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutService {

    StockInOut save(StockInOut io);

    List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                            String vouNo, String userCode, String vouStatus);

    StockInOut findById(StockIOKey id);

    void delete(StockIOKey key) throws Exception;

    void restore(StockIOKey key) throws Exception;

    List<StockInOut> unUpload(String syncDate);

    Date getMaxDate();

    List<StockInOut> search(String updatedDate, List<LocationKey> keys);

}
