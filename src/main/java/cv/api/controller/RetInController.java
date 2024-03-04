/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisDetail;
import cv.api.entity.RetInHisKey;
import cv.api.model.VReturnIn;
import cv.api.repo.AccountRepo;
import cv.api.service.ReportService;
import cv.api.service.RetInDetailService;
import cv.api.service.RetInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/retin")
@Slf4j
@RequiredArgsConstructor
public class RetInController {

    private final ReturnObject ro = ReturnObject.builder().build();
    private final RetInService riService;
    private final RetInDetailService rdService;
    private final ReportService reportService;
    private final AccountRepo accountRepo;

    @PostMapping(path = "/saveReturnIn")
    public Mono<RetInHis> saveReturnIn(@RequestBody RetInHis retin) {
        retin.setUpdatedDate(Util1.getTodayLocalDate());
        retin = riService.save(retin);
        //send message to service
        accountRepo.sendReturnIn(retin);
        //send to cloud
        return Mono.justOrEmpty(retin);
    }

    @PostMapping(path = "/getReturnIn")
    public Flux<?> getReturnIn(@RequestBody ReportFilter filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        List<VReturnIn> listRI = reportService.getReturnInHistory(fromDate, toDate, cusCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted, projectNo, curCode);
        return Flux.fromIterable(listRI).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteReturnIn")
    public Mono<?> deleteReturnIn(@RequestBody RetInHisKey key) throws Exception {
        riService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreReturnIn")
    public Mono<?> restoreReturnIn(@RequestBody RetInHisKey key) throws Exception {
        riService.restore(key);
        ro.setMessage("Restored.");
        return Mono.just(true);
    }

    @PostMapping(path = "/findReturnIn")
    public Mono<RetInHis> findReturnIn(@RequestBody RetInHisKey key) {
        RetInHis sh = riService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getReturnInDetail")
    public Flux<?> getReturnInDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<RetInHisDetail> listSD = rdService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD).onErrorResume(throwable -> Flux.empty());
    }
}
