/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.*;
import cv.api.repo.AccountRepo;
import cv.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/milling")
@Slf4j
public class MillingController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private MillingHisService hService;
    @Autowired
    private MillingRawService rawService;
    @Autowired
    private MillingOutService outService;
    @Autowired
    private MillingExpenseService expService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping(path = "/save-milling")
    public Mono<?> saveSale(@RequestBody MillingHis sale) {
        sale.setUpdatedDate(Util1.getTodayLocalDate());
        //if change location
        if (isValidSale(sale, ro)) {
            sale = hService.save(sale);
        } else {
            return Mono.justOrEmpty(ro);
        }
        //for account
//        accountRepo.sendSale(sale);
        //for cloud
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

    @PostMapping(path = "/get-milling")
    public Flux<?> getSale(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String ref = Util1.isNull(filter.getReference(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        Integer deptId = filter.getDeptId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        List<MillingHis> listPur = reportService.getMillingHistory(fromDate, toDate, cusCode, vouNo, remark, ref, userCode,
                stockCode, locCode, compCode, deptId, deleted, projectNo,curCode);
        return Flux.fromIterable(listPur).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/delete-milling")
    public Mono<?> deleteSale(@RequestBody MillingHisKey key) throws Exception {
        hService.delete(key);
        //delete in account
//        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        return Mono.just(true);
    }

    @PostMapping(path = "/restore-milling")
    public Mono<?> restoreSale(@RequestBody MillingHisKey key) throws Exception {
        hService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/find-milling")
    public Mono<MillingHis> findSale(@RequestBody MillingHisKey key) {
        MillingHis sh = hService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/get-raw-detail")
    public Flux<?> getRawDetail(@RequestParam String vouNo,
                                 @RequestParam String compCode,
                                 @RequestParam Integer deptId) {
        return Flux.fromIterable(rawService.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-expense-detail")
    public Flux<?> getExpenseDetail(@RequestParam String vouNo,
                                    @RequestParam String compCode,
                                    @RequestParam Integer deptId) {
        return Flux.fromIterable(expService.search(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/get-output-detail")
    public Flux<?> getMillingDetail(@RequestParam String vouNo,
                                    @RequestParam String compCode,
                                    @RequestParam Integer deptId) {
        return Flux.fromIterable(outService.search(vouNo, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }
}
