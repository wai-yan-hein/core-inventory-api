/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.RetOutHis;
import cv.api.entity.RetOutHisDetail;
import cv.api.entity.RetOutHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.ReportService;
import cv.api.service.RetOutDetailService;
import cv.api.service.RetOutService;
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
@RequestMapping("/retout")
@Slf4j
public class RetOutController {

    @Autowired
    private RetOutService roService;
    @Autowired
    private RetOutDetailService rdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping(path = "/saveReturnOut")
    public Mono<?> saveReturnOut(@RequestBody RetOutHis retout) {
        retout.setUpdatedDate(Util1.getTodayLocalDate());
        retout = roService.save(retout);
        accountRepo.sendReturnOut(retout);
        return Mono.justOrEmpty(retout);
    }

    @PostMapping(path = "/getReturnOut")
    public Flux<?> getReturnOut(@RequestBody ReportFilter filter) throws Exception {
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
        return Flux.fromIterable(reportService.getReturnOutHistory(fromDate, toDate, cusCode, vouNo, remark, userCode,
                stockCode, locCode, compCode, deptId, deleted, projectNo, curCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteReturnOut")
    public Mono<?> deleteReturnOut(@RequestBody RetOutHisKey key) throws Exception {
        roService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        return Mono.just(true);
    }

    @PostMapping(path = "/restoreReturnOut")
    public Mono<?> restoreReturnOut(@RequestBody RetOutHisKey key) throws Exception {
        roService.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/findReturnOut")
    public Mono<RetOutHis> findReturnOut(@RequestBody RetOutHisKey key) {
        RetOutHis sh = roService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/getReturnOutDetail")
    public Flux<?> getReturnOutDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<RetOutHisDetail> listSD = rdService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD).onErrorResume(throwable -> Flux.empty());
    }
}
