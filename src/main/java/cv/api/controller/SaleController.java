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
import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisDetail;
import cv.api.inv.entity.SaleHisKey;
import cv.api.inv.service.BackupService;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.SaleDetailService;
import cv.api.inv.service.SaleHisService;
import cv.api.inv.view.VSale;
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
    public ResponseEntity<?> saveSale(@RequestBody SaleHis sale) throws Exception {
        //if change location
        cloudMQSender.checkLocationAndTruncate(sale);
        if (isValidSale(sale, ro)) {
            backupService.backup(sale);
            sale = shService.save(sale);
        }
        //for account
        accountRepo.sendSale(sale);
        //for cloud
        cloudMQSender.send(sale);
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
        } else if (listSH.size() <= 1) {
            status = false;
            ro.setMessage("Invalid Voucher Records.");
        }
        return status;
    }

    @PostMapping(path = "/get-sale")
    public ResponseEntity<List<VSale>> getSale(@RequestBody FilterObject filter) throws Exception {
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
        List<VSale> saleList = reportService.getSaleHistory(fromDate, toDate, cusCode, saleManCode, vouNo, remark, reference, userCode, stockCode, locCode, compCode, deptId, deleted);
        return ResponseEntity.ok(saleList);
    }

    @PostMapping(path = "/delete-sale")
    public ResponseEntity<ReturnObject> deleteSale(@RequestBody SaleHisKey key) throws Exception {
        shService.delete(key);
        ro.setMessage("Deleted.");
        cloudMQSender.delete(key);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-sale")
    public ResponseEntity<ReturnObject> restoreSale(@RequestBody SaleHisKey key) throws Exception {
        shService.restore(key);
        ro.setMessage("Restored.");
        cloudMQSender.restore(key);
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
}
