/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.entity.MillingHis;
import cv.api.entity.MillingHisKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface MillingHisDao {

    MillingHis save(MillingHis sh);

    List<MillingHis> getMillingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference,
                                       String userCode, String stockCode, String locCode, String compCode,
                                       Integer deptId, boolean deleted, String projectNo, String curCode);

    MillingHis findById(MillingHisKey id);

    void delete(MillingHisKey key) throws Exception;

    void restore(MillingHisKey key) throws Exception;

    List<MillingHis> unUploadVoucher(LocalDateTime syncDate);

    List<MillingHis> unUpload(String syncDate);


    void truncate(MillingHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

}
