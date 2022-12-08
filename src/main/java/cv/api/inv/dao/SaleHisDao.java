/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.LocationKey;
import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface SaleHisDao {

    SaleHis save(SaleHis sh);

    List<SaleHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    SaleHis findById(SaleHisKey id);

    void delete(SaleHisKey key) throws Exception;

    void restore(SaleHisKey key) throws Exception;

    List<SaleHis> unUploadVoucher(String syncDate);

    List<SaleHis> unUpload();

    Date getMaxDate();

    List<SaleHis> search(String updatedDate, List<LocationKey> keys);

}
