/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.PurHis;
import cv.api.entity.PurHisDetail;
import cv.api.entity.PurHisKey;
import cv.api.model.VPurchase;
import cv.api.repo.AccountRepo;
import cv.api.service.PurHisDetailService;
import cv.api.service.PurHisService;
import cv.api.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/pur")
@Slf4j
public class PurchaseController {

    @Autowired
    private PurHisService phService;
    @Autowired
    private PurHisDetailService pdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping(path = "/save-pur")
    public Mono<PurHis> savePurchase(@RequestBody PurHis pur) {
        pur.setUpdatedDate(LocalDateTime.now());
        pur = phService.save(pur);
        //send message to service
        accountRepo.sendPurchase(pur);
        return Mono.justOrEmpty(pur);
    }

    @PostMapping(path = "/get-pur")
    public Flux<?> getPur(@RequestBody FilterObject filter) throws Exception {
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
        String deleted = String.valueOf(filter.isDeleted());
        Integer deptId = filter.getDeptId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        List<VPurchase> listPur = reportService.getPurchaseHistory(fromDate, toDate, cusCode, vouNo, remark, ref, userCode,
                stockCode, locCode, compCode, deptId, deleted, projectNo,curCode);
        return Flux.fromIterable(listPur);
    }

    @PostMapping(path = "/delete-pur")
    public Mono<?> deletePur(@RequestBody PurHisKey key) throws Exception {
        phService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restore-pur")
    public Mono<?> restorePur(@RequestBody PurHisKey key) throws Exception {
        phService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/find-pur")
    public Mono<PurHis> findPur(@RequestBody PurHisKey key) {
        PurHis sh = phService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/get-pur-detail")
    public Flux<PurHisDetail> getPurDetail(@RequestParam String vouNo,
                                           @RequestParam String compCode,
                                           @RequestParam Integer deptId) {
        List<PurHisDetail> listSD = pdService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD);
    }
}
