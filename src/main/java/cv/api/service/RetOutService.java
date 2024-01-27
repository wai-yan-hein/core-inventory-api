/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.RetOutHis;
import cv.api.entity.RetOutHisKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetOutService {

    RetOutHis save(RetOutHis saleHis);

    RetOutHis update(RetOutHis ro);

    List<RetOutHis> search(String fromDate, String toDate, String cusCode,
                           String vouNo, String remark, String userCode);

    RetOutHis findById(RetOutHisKey id);

    void delete(RetOutHisKey key) throws Exception;

    void restore(RetOutHisKey key) throws Exception;


    List<RetOutHis> unUploadVoucher(LocalDateTime syncDate);

    List<RetOutHis> unUpload(String syncDate);

    List<RetOutHis> search(String updatedDate, List<String> keys);

}
