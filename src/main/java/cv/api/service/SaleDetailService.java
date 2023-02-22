/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.SaleHisDetail;

import java.util.List;

/**
 * @author wai yan
 */
public interface SaleDetailService {

    SaleHisDetail save(SaleHisDetail sdh);

    List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(String code, String compCode, Integer deptId);
    List<SaleHisDetail> getSaleByBatch(String batchNo, String compCode, Integer depId);

}
