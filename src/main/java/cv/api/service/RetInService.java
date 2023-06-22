/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetInService {

    RetInHis save(RetInHis ri);

    RetInHis update(RetInHis ri);

    List<RetInHis> search(String fromDate, String toDate, String cusCode,
                          String vouNo, String remark, String userCode);

    RetInHis findById(RetInHisKey id);

    void delete(RetInHisKey key) throws Exception;

    void restore(RetInHisKey key) throws Exception;


    List<RetInHis> unUploadVoucher(LocalDateTime syncDate);

    List<RetInHis> unUpload(String syncDate);

    Date getMaxDate();

    List<RetInHis> search(String updatedDate, List<String> keys);

    void truncate(RetInHisKey key);
}
