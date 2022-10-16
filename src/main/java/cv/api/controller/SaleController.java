/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.MessageSender;
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
import io.swagger.models.auth.In;
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

    @Autowired
    private SaleHisService shService;
    @Autowired
    private SaleDetailService sdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private AccountRepo accountRepo;

    @PostMapping(path = "/save-sale")
    public ResponseEntity<SaleHis> saveSale(@RequestBody SaleHis sale) throws Exception {
        if (isValidSale(sale, ro)) {
            backupService.backup(sale);
            sale = shService.save(sale);
        }

        //send message to service
        try {
            messageSender.sendMessage("SALE", sale.getKey().getVouNo());
        } catch (Exception e) {
            SaleHis sh = shService.findById(sale.getKey());
            sh.setIntgUpdStatus(null);
            shService.update(sh);
            log.error(String.format("sendMessage: SALE %s", e.getMessage()));
        }
        accountRepo.sendSale(sale);
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
        List<VSale> saleList = reportService.getSaleHistory(fromDate, toDate, cusCode, saleManCode, vouNo, remark, reference, userCode, stockCode, locCode, compCode);
        return ResponseEntity.ok(saleList);
    }

    @DeleteMapping(path = "/delete-sale")
    public ResponseEntity<ReturnObject> deleteSale(@RequestParam String code) throws Exception {
        shService.delete(code);
        ro.setMessage("Deleted.");
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
        List<SaleHisDetail> listSD = sdService.search(vouNo,compCode,deptId);
        return ResponseEntity.ok(listSD);
    }
}