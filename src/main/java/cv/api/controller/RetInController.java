/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.cloud.CloudMQSender;
import cv.api.common.FilterObject;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class RetInController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private RetInService riService;
    @Autowired
    private RetInDetailService rdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;

    @PostMapping(path = "/save-retin")
    public ResponseEntity<RetInHis> saveReturnIn(@RequestBody RetInHis retin) {
        retin.setUpdatedDate(Util1.getTodayDate());
        retin = riService.save(retin);
        //send message to service
        accountRepo.sendReturnIn(retin);
        //send to cloud
        if (cloudMQSender != null) cloudMQSender.send(retin);
        return ResponseEntity.ok(retin);
    }

    @PostMapping(path = "/get-retin")
    public Flux<?> getRI(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String projectNo = Util1.isAll(filter.getProjectNo());
        List<VReturnIn> listRI = reportService.getReturnInHistory(fromDate, toDate, cusCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted, projectNo);
        return Flux.fromIterable(listRI);
    }

    @PostMapping(path = "/delete-retin")
    public Mono<?> deleteRI(@RequestBody RetInHisKey key) throws Exception {
        riService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        if (cloudMQSender != null) cloudMQSender.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restore-retin")
    public Mono<?> restoreRI(@RequestBody RetInHisKey key) throws Exception {
        riService.restore(key);
        ro.setMessage("Restored.");
        if (cloudMQSender != null) cloudMQSender.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/find-retin")
    public ResponseEntity<RetInHis> findRI(@RequestBody RetInHisKey key) {
        RetInHis sh = riService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-retin-detail")
    public ResponseEntity<List<RetInHisDetail>> getRIDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<RetInHisDetail> listSD = rdService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }
}
