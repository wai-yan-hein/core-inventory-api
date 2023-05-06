/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.OrderHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface OrderDetailService {

    OrderHisDetail save(OrderHisDetail ohd);

    List<OrderHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(String vouNo, Integer uniqueId, String compCode, Integer deptId);

//    List<OrderHisDetail> getOrderByBatch(String batchNo, String compCode, Integer depId);

//    List<OrderHisDetail> getOrderByBatchDetail(String batchNo, String compCode, Integer depId);


}
