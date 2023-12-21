/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisKey;
import cv.api.model.VOrder;
import jakarta.persistence.criteria.Order;

import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
public interface OrderHisDao {

    OrderHis save(OrderHis sh);
    void update(OrderHis oh);

    List<VOrder> getOrderHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
                                 String remark, String reference, String userCode, String stockCode, String locCode,
                                 String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
                                 String projectNo, String curCode, String orderStatus);

    OrderHis findById(OrderHisKey id);

    void delete(OrderHisKey key) throws Exception;

    void restore(OrderHisKey key) throws Exception;

    List<OrderHis> unUploadVoucher(String syncDate);

    List<OrderHis> unUpload(String syncDate);

    Date getMaxDate();

    void truncate(OrderHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

}
