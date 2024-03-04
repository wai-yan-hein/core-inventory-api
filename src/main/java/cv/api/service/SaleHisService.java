/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.General;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;
import cv.api.entity.SaleNote;
import cv.api.entity.VouDiscount;
import cv.api.model.VSale;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface SaleHisService {

    SaleHis save(SaleHis saleHis);

    void update(SaleHis saleHis);

    List<SaleHis> search(String fromDate, String toDate, String cusCode,
                         String vouNo, String remark, String userCode);

    SaleHis findById(SaleHisKey id);

    void delete(SaleHisKey key);

    void restore(SaleHisKey key) throws Exception;


    List<SaleHis> unUploadVoucher(LocalDateTime syncDate);

    List<SaleHis> unUpload(String syncDate);

    void truncate(SaleHisKey key);

    General getVoucherInfo(String vouDate, String compCode, Integer depId);

    Flux<VouDiscount> getVoucherDiscount(String vouNo, String compCode);
    Flux<SaleNote> getSaleNote(String vouNo, String compCode);


    List<VouDiscount> searchDiscountDescription(String str, String compCode);

    Flux<VSale> getSale(ReportFilter filterObject);
    Mono<Boolean> updatePost(String vouNo, String compCode, boolean post);
}
