/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.MillingHis;
import cv.api.entity.MillingHisKey;
import cv.api.service.MillingHisService;
import cv.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/milling")
@Slf4j
@RequiredArgsConstructor
public class MillingController {

    private final ReturnObject ro = ReturnObject.builder().build();
    private final MillingHisService millingHisService;
    private final ReportService reportService;

    @PostMapping(path = "/saveMilling")
    public Mono<?> saveMilling(@RequestBody MillingHis sale) {
        sale.setUpdatedDate(Util1.getTodayLocalDate());
        //if change location
        if (isValidSale(sale, ro)) {
            sale = millingHisService.save(sale);
        } else {
            return Mono.justOrEmpty(ro);
        }
        return Mono.justOrEmpty(sale);
    }

    private boolean isValidSale(MillingHis sale, ReturnObject ro) {
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
        } else if (Util1.isNullOrEmpty(sale.getCreatedBy())) {
            status = false;
            ro.setMessage("Invalid Created User.");
        } else if (Util1.isNullOrEmpty(sale.getCreatedDate())) {
            status = false;
            ro.setMessage("Invalid Created Date.");
        }
        return status;
    }

    @PostMapping(path = "/getMilling")
    public Flux<?> getMilling(@RequestBody ReportFilter filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String ref = Util1.isNull(filter.getReference(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        Integer deptId = filter.getDeptId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        String jobNo = Util1.isNull(filter.getJobNo(), "-");
        return reportService.getMillingHistory(fromDate, toDate, cusCode, vouNo, remark, ref, userCode,
                stockCode, locCode, compCode, deptId, deleted, projectNo, curCode, jobNo);
    }

    @PostMapping(path = "/deleteMilling")
    public Mono<?> deleteSale(@RequestBody MillingHisKey key) throws Exception {
        millingHisService.delete(key);
        //delete in account
//        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreMilling")
    public Mono<?> restoreMilling(@RequestBody MillingHisKey key) throws Exception {
        millingHisService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/findMilling")
    public Mono<MillingHis> findMilling(@RequestBody MillingHisKey key) {
        MillingHis sh = millingHisService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getRawDetail")
    public Flux<?> getRawDetail(@RequestParam String vouNo,
                                @RequestParam String compCode,
                                @RequestParam Integer deptId) {
        return Flux.fromIterable(millingHisService.getMillingRaw(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getMillingExpense")
    public Flux<?> getExpenseDetail(@RequestParam String vouNo,
                                    @RequestParam String compCode) {
        return Flux.fromIterable(millingHisService.getMillingExpense(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getOutputDetail")
    public Flux<?> getOutputDetail(@RequestParam String vouNo,
                                   @RequestParam String compCode,
                                   @RequestParam Integer deptId) {
        return Flux.fromIterable(millingHisService.getMillingOut(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUsageDetail")
    public Flux<?> getUsageDetail(@RequestParam String vouNo,
                                  @RequestParam String compCode) {
        return Flux.fromIterable(millingHisService.getMillingUsage(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }
}
