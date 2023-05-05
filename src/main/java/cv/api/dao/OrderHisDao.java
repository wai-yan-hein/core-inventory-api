/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisKey;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface OrderHisDao {

    OrderHis save(OrderHis sh);

    List<OrderHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    OrderHis findById(OrderHisKey id);

    void delete(OrderHisKey key) throws Exception;

    void restore(OrderHisKey key) throws Exception;

    List<OrderHis> unUploadVoucher(String syncDate);

    List<OrderHis> unUpload(String syncDate);

    Date getMaxDate();

    List<OrderHis> search(String updatedDate, List<String> location);

    void truncate(OrderHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

}
