/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wai yan
 */
public interface SaleDetailService {

    SaleHisDetail save(SaleHisDetail sdh);

    List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId);

    int delete(SaleDetailKey key);

    List<SaleHisDetail> getSaleByBatch(String batchNo, String compCode, Integer depId);

    List<SaleHisDetail> getSaleByBatchDetail(String batchNo, String compCode, Integer depId);



}
