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
import cv.api.inv.entity.RetOutHis;
import cv.api.inv.entity.RetOutHisDetail;
import cv.api.inv.entity.RetOutHisKey;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.RetOutDetailService;
import cv.api.inv.service.RetOutService;
import cv.api.inv.view.VReturnOut;
import cv.api.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/retout")
@Slf4j
public class RetOutController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private RetOutService roService;
    @Autowired
    private RetOutDetailService rdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;

    @PostMapping(path = "/save-retout")
    public ResponseEntity<?> saveReturnOut(@RequestBody RetOutHis retout) throws Exception {
        retout = roService.save(retout);
        accountRepo.sendReturnOut(retout);
        //send to cloud
        cloudMQSender.send(retout);
        return ResponseEntity.ok(retout);
    }

    @PostMapping(path = "/get-retout")
    public ResponseEntity<List<VReturnOut>> getRO(@RequestBody FilterObject filter) throws Exception {
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
        List<VReturnOut> listRO = reportService.getReturnOutHistory(fromDate, toDate, cusCode, vouNo, remark, userCode, stockCode, locCode, compCode, deptId, deleted);
        return ResponseEntity.ok(listRO);
    }

    @PostMapping(path = "/delete-retout")
    public ResponseEntity<ReturnObject> deleteRO(@RequestBody RetOutHisKey key) throws Exception {
        roService.delete(key);
        ro.setMessage("Deleted.");
        cloudMQSender.delete(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-retout")
    public ResponseEntity<ReturnObject> restoreRo(@RequestBody RetOutHisKey key) throws Exception {
        roService.restore(key);
        ro.setMessage("Restored.");
        cloudMQSender.restore(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-retout")
    public ResponseEntity<RetOutHis> findRO(@RequestBody RetOutHisKey key) {
        RetOutHis sh = roService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-retout-detail")
    public ResponseEntity<List<RetOutHisDetail>> getRODetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<RetOutHisDetail> listSD = rdService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }
}
