/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.entity.*;
import cv.api.model.VSale;
import cv.api.repo.AccountRepo;
import cv.api.service.SaleDetailService;
import cv.api.service.SaleHisService;
import cv.api.service.SaleOrderJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/sale")
@Slf4j
@RequiredArgsConstructor
public class SaleController {

    private final SaleHisService shService;
    private final SaleDetailService sdService;
    private final AccountRepo accountRepo;
    private final SaleOrderJoinService saleOrderJoinService;

    @PostMapping(path = "/saveSale")
    public Mono<SaleHis> saveSale(@RequestBody SaleHis sale) {
        return shService.save(sale).flatMap(obj -> accountRepo.sendSaleAsync(obj).thenReturn(obj));
    }

    @PostMapping(path = "/getSale")
    public Flux<VSale> getSale(@RequestBody ReportFilter filter) {
        return shService.getSale(filter);
    }

    @PostMapping(path = "/deleteSale")
    public Mono<Boolean> deleteSale(@RequestBody SaleHisKey key) {
        return shService.delete(key).flatMap(aBoolean -> Mono.defer(() -> {
            accountRepo.deleteInvVoucher(key);
            return Mono.just(aBoolean);
        }));
    }

    @PutMapping(path = "/updateSPay")
    public Mono<Boolean> updateSPay(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam boolean sPay) {
        return shService.updateSPay(vouNo, compCode, sPay);
    }


    @PostMapping(path = "/restoreSale")
    public Mono<Boolean> restoreSale(@RequestBody SaleHisKey key) {
        return shService.restore(key);
    }

    @PostMapping(path = "/findSale")
    public Mono<SaleHis> findSale(@RequestBody SaleHisKey key) {
        return shService.findById(key);
    }

    @GetMapping(path = "/getSaleDetail")
    public Flux<SaleHisDetail> getSaleDetail(@RequestParam String vouNo,
                                             @RequestParam String compCode) {
        return sdService.search(vouNo, compCode);
    }

    @GetMapping(path = "/getSaleVoucherInfo")
    public Mono<General> getSaleVoucherInfo(@RequestParam String vouDate,
                                            @RequestParam String compCode) {
        return shService.getVoucherInfo(vouDate, compCode);
    }

    @GetMapping(path = "/getSaleByBatch")
    public Flux<SaleHisDetail> getSaleByBatch(@RequestParam String batchNo,
                                              @RequestParam String compCode,
                                              @RequestParam boolean detail) {
        return detail ? sdService.getSaleByBatchDetail(batchNo, compCode) : sdService.getSaleByBatch(batchNo, compCode);
    }

    @GetMapping(path = "/getVoucherDiscount")
    public Flux<VouDiscount> getVoucherDiscount(@RequestParam String vouNo,
                                                @RequestParam String compCode) {
        return shService.getVoucherDiscount(vouNo, compCode);
    }


    @GetMapping(path = "/searchDiscountDescription")
    public Flux<VouDiscount> searchDiscountDescription(@RequestParam String str,
                                                       @RequestParam String compCode) {
        return shService.searchDiscountDescription(str, compCode);
    }

    @GetMapping(path = "/getSaleOrder")
    public Flux<SaleOrderJoin> getSaleOrder(@RequestParam String vouNo,
                                            @RequestParam String compCode) {
        return saleOrderJoinService.getSaleOrder(vouNo, compCode);
    }

    @PostMapping(path = "/getSaleSummaryByDepartment")
    public Flux<VSale> getSaleSummaryByDepartment(@RequestBody ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return shService.getSaleSummaryByDepartment(fromDate, toDate, compCode);
    }
}
