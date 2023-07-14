/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.MilingHis;
import cv.api.entity.MilingHisKey;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface MilingHisService {

    MilingHis save(MilingHis milingHis);

    MilingHis update(MilingHis milingHis);

    List<MilingHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    MilingHis findById(MilingHisKey id);

    void delete(MilingHisKey key) throws Exception;

    void restore(MilingHisKey key) throws Exception;


    List<MilingHis> unUploadVoucher(LocalDateTime syncDate);

    List<MilingHis> unUpload(String syncDate);

    Date getMaxDate();

    void truncate(MilingHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);


}
