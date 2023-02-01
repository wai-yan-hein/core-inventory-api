/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface SaleHisService {

    SaleHis save(SaleHis saleHis);

    SaleHis update(SaleHis saleHis);

    List<SaleHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    SaleHis findById(SaleHisKey id);

    void delete(SaleHisKey key) throws Exception;

    void restore(SaleHisKey key) throws Exception;


    List<SaleHis> unUploadVoucher(String syncDate);

    List<SaleHis> unUpload(String syncDate);

    Date getMaxDate();

    List<SaleHis> search(String updatedDate, List<String> keys);

    void truncate(SaleHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);
}
