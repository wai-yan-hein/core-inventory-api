/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisDetail;
import cv.api.entity.RetInHisKey;
import cv.api.model.VReturnIn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface RetInService {

    Mono<RetInHis> save(RetInHis ri);


    Mono<RetInHis> findById(RetInHisKey id);

    Mono<Boolean> delete(RetInHisKey key) ;

    Mono<Boolean> restore(RetInHisKey key);


    Flux<RetInHis> unUploadVoucher(LocalDateTime syncDate);
    Flux<VReturnIn> getHistory(ReportFilter filter);


    Flux<RetInHisDetail> search(String vouNo, String compCode);
}
