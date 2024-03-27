/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
public interface SaleDetailService {

    Mono<SaleHisDetail> save(SaleHisDetail sdh);

    Flux<SaleHisDetail> search(String vouNo, String compCode);


    Flux<SaleHisDetail> getSaleByBatch(String batchNo, String compCode);

    Flux<SaleHisDetail> getSaleByBatchDetail(String batchNo, String compCode);
    Mono<Boolean> delete(String vouNo,String compCode);



}
