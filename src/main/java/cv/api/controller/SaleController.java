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
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisDetail;
import cv.api.entity.SaleHisKey;
import cv.api.repo.AccountRepo;
import cv.api.service.BackupService;
import cv.api.service.ReportService;
import cv.api.service.SaleDetailService;
import cv.api.service.SaleHisService;
import cv.api.model.VSale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
    private BackupService backupService;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;

    @PostMapping(path = "/save-sale")
    public ResponseEntity<?> saveSale(@RequestBody SaleHis sale) {
        sale.setUpdatedDate(Util1.getTodayDate());
        //if change location
        if (cloudMQSender != null) cloudMQSender.checkLocationAndTruncate(sale);
        if (isValidSale(sale, ro)) {
            backupService.backup(sale);
            sale = shService.save(sale);
        } else {
            return ResponseEntity.ok(ro);
        }
        //for account
        accountRepo.sendSale(sale);
        //for cloud
        if (cloudMQSender != null) cloudMQSender.send(sale);
        return ResponseEntity.ok(sale);
    }

    private boolean isValidSale(SaleHis sale, ReturnObject ro) {
        boolean status = true;
        List<SaleHisDetail> listSH = sale.getListSH();
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

    @PostMapping(path = "/get-sale")
    public Flux<?> getSale(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String saleManCode = Util1.isNull(filter.getSaleManCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String nullBatch = String.valueOf(filter.isNullBatch());
        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        List<VSale> saleList = reportService.getSaleHistory(fromDate, toDate, cusCode, saleManCode, vouNo, remark,
                reference, userCode, stockCode, locCode, compCode, deptId, deleted, nullBatch, batchNo);
        return Flux.fromIterable(saleList);
    }

    @PostMapping(path = "/delete-sale")
    public ResponseEntity<ReturnObject> deleteSale(@RequestBody SaleHisKey key) throws Exception {
        shService.delete(key);
        //delete in account
        accountRepo.deleteInvVoucher(key);
        //delete in cloud
        if (cloudMQSender != null) cloudMQSender.delete(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-sale")
    public ResponseEntity<ReturnObject> restoreSale(@RequestBody SaleHisKey key) throws Exception {
        shService.restore(key);
        ro.setMessage("Restored.");
        if (cloudMQSender != null) cloudMQSender.restore(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-sale")
    public ResponseEntity<SaleHis> findSale(@RequestBody SaleHisKey key) {
        SaleHis sh = shService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-sale-detail")
    public ResponseEntity<List<SaleHisDetail>> getSaleDetail(@RequestParam String vouNo,
                                                             @RequestParam String compCode,
                                                             @RequestParam Integer deptId) {
        List<SaleHisDetail> listSD = sdService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }

    @GetMapping(path = "/get-sale-voucher-info")
    public ResponseEntity<?> getSaleVoucherCount(@RequestParam String vouDate,
                                                 @RequestParam String compCode,
                                                 @RequestParam Integer deptId) {
        return ResponseEntity.ok(shService.getVoucherInfo(vouDate, compCode, deptId));
    }

    @GetMapping(path = "/get-sale-by-batch")
    public ResponseEntity<?> getSaleByBatch(@RequestParam String batchNo,
                                            @RequestParam String compCode,
                                            @RequestParam Integer deptId) {
        return ResponseEntity.ok(sdService.getSaleByBatch(batchNo, compCode, deptId));
    }
}
