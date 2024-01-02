/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;
import cv.api.model.VSale;
import cv.api.repo.AccountRepo;
import cv.api.service.ReportService;
import cv.api.service.SaleDetailService;
import cv.api.service.SaleHisService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/sale")
@Slf4j
public class SaleController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private SaleHisService shService;
    @Autowired
    private SaleDetailService sdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping(path = "/saveSale")
    public Mono<?> saveSale(@NotNull @RequestBody SaleHis sale) {
        sale.setUpdatedDate(Util1.getTodayLocalDate());
        //if change location
        if (isValidSale(sale, ro)) {
            if (sale.getExpense() == null) {
                sale.setExpense(0.0);
            }
            sale = shService.save(sale);
        } else {
            return Mono.justOrEmpty(ro);
        }
        //for account
        accountRepo.sendSale(sale);
        //for cloud
        return Mono.justOrEmpty(sale);
    }

    private boolean isValidSale(@NotNull SaleHis sale, ReturnObject ro) {
        boolean status = true;
        if (Util1.isNullOrEmpty(sale.getTraderCode())) {
            status = false;
            ro.setMessage("Invalid Trader.");
        } else if (Util1.isNullOrEmpty(sale.getVouDate())) {
            status = false;
            ro.setMessage("Invalid Voucher Date.");
        } else if (Util1.isNullOrEmpty(sale.getCurCode())) {
            status = false;
            ro.setMessage("Invalid Currency.");
        } else if (Util1.getFloat(sale.getVouTotal()) <= 0) {
            status = false;
            ro.setMessage("Invalid Voucher Total.");
        } else if (Util1.isNullOrEmpty(sale.getLocCode())) {
            status = false;
            ro.setMessage("Invalid Location.");
        } else if (Util1.isNullOrEmpty(sale.getCreatedBy())) {
            status = false;
            ro.setMessage("Invalid Created User.");
        } else if (Util1.isNullOrEmpty(sale.getCreatedDate())) {
            status = false;
            ro.setMessage("Invalid Created Date.");
        }
        return status;
    }

//    @PostMapping(path = "/getSale")
//    public Flux<?> getSale(@NotNull @RequestBody FilterObject filter) {
//        String fromDate = Util1.isNull(filter.getFromDate(), "-");
//        String toDate = Util1.isNull(filter.getToDate(), "-");
//        String vouNo = Util1.isNull(filter.getVouNo(), "-");
//        String userCode = Util1.isNull(filter.getUserCode(), "-");
//        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
//        String remark = Util1.isNull(filter.getRemark(), "-");
//        String stockCode = Util1.isNull(filter.getStockCode(), "-");
//        String saleManCode = Util1.isNull(filter.getSaleManCode(), "-");
//        String reference = Util1.isNull(filter.getReference(), "-");
//        String compCode = filter.getCompCode();
//        String locCode = Util1.isNull(filter.getLocCode(), "-");
//        Integer deptId = filter.getDeptId();
//        String deleted = String.valueOf(filter.isDeleted());
//        String nullBatch = String.valueOf(filter.isNullBatch());
//        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
//        String projectNo = Util1.isAll(filter.getProjectNo());
//        String curCode = Util1.isAll(filter.getCurCode());
//        List<VSale> saleList = reportService.getSaleHistory(fromDate, toDate, cusCode, saleManCode, vouNo, remark,
//                reference, userCode, stockCode, locCode, compCode, deptId, deleted, nullBatch, batchNo, projectNo, curCode);
//        return Flux.fromIterable(saleList).onErrorResume(throwable -> Flux.empty());
//    }

    @PostMapping(path = "/getSale")
    public Flux<?> getSale(@NotNull @RequestBody FilterObject filter) {
         return shService.getSale(filter);
    }

    @PostMapping(path = "/deleteSale")
    public Mono<?> deleteSale(@RequestBody SaleHisKey key) {
        shService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreSale")
    public Mono<?> restoreSale(@RequestBody SaleHisKey key) throws Exception {
        shService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/findSale")
    public Mono<SaleHis> findSale(@RequestBody SaleHisKey key) {
        SaleHis sh = shService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getSaleDetail")
    public Flux<?> getSaleDetail(@RequestParam String vouNo,
                                 @RequestParam String compCode,
                                 @RequestParam Integer deptId) {
        return Flux.fromIterable(sdService.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getSaleVoucherInfo")
    public Mono<?> getSaleVoucherInfo(@RequestParam String vouDate,
                                      @RequestParam String compCode,
                                      @RequestParam Integer deptId) {
        return Mono.justOrEmpty(shService.getVoucherInfo(vouDate, compCode, deptId));
    }

    @GetMapping(path = "/getSaleByBatch")
    public Flux<?> getSaleByBatch(@RequestParam String batchNo,
                                  @RequestParam String compCode,
                                  @RequestParam Integer deptId,
                                  @RequestParam boolean detail) {
        if (detail) {
            return Flux.fromIterable(sdService.getSaleByBatchDetail(batchNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
        }
        return Flux.fromIterable(sdService.getSaleByBatch(batchNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getVoucherDiscount")
    public Flux<?> getVoucherDiscount(@RequestParam String vouNo,
                                      @RequestParam String compCode) {
        return Flux.fromIterable(shService.getVoucherDiscount(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/searchDiscountDescription")
    public Flux<?> searchDiscountDescription(@RequestParam String str,
                                             @RequestParam String compCode) {
        return Flux.fromIterable(shService.searchDiscountDescription(str, compCode)).onErrorResume(throwable -> Flux.empty());
    }


}
