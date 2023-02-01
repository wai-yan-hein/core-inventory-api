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
import cv.api.entity.PurHis;
import cv.api.entity.PurHisDetail;
import cv.api.entity.PurHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.PurHisDetailService;
import cv.api.service.PurHisService;
import cv.api.service.ReportService;
import cv.api.model.VPurchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/pur")
@Slf4j
public class PurchaseController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private PurHisService phService;
    @Autowired
    private PurHisDetailService pdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;

    @PostMapping(path = "/save-pur")
    public ResponseEntity<PurHis> savePurchase(@RequestBody PurHis pur) {
        pur.setUpdatedDate(Util1.getTodayDate());
        pur = phService.save(pur);
        //send message to service
        accountRepo.sendPurchase(pur);
        //send to cloud
        if (cloudMQSender != null) cloudMQSender.send(pur);
        return ResponseEntity.ok(pur);
    }

    @PostMapping(path = "/get-pur")
    public ResponseEntity<List<VPurchase>> getPur(@RequestBody FilterObject filter) throws Exception {
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
        List<VPurchase> listPur = reportService.getPurchaseHistory(fromDate, toDate, cusCode, vouNo, remark, ref, userCode, stockCode, locCode, compCode, deptId, deleted);
        return ResponseEntity.ok(listPur);
    }

    @PostMapping(path = "/delete-pur")
    public ResponseEntity<ReturnObject> deletePur(@RequestBody PurHisKey key) throws Exception {
        phService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        if (cloudMQSender != null) cloudMQSender.delete(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-pur")
    public ResponseEntity<?> restorePur(@RequestBody PurHisKey key) throws Exception {
        phService.restore(key);
        ro.setMessage("Restored.");
        if (cloudMQSender != null) cloudMQSender.restore(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-pur")
    public ResponseEntity<PurHis> findPur(@RequestBody PurHisKey key) {
        PurHis sh = phService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-pur-detail")
    public ResponseEntity<List<PurHisDetail>> getPurDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<PurHisDetail> listSD = pdService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }
}
