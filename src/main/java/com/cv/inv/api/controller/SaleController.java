/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.MessageSender;
import com.cv.inv.api.common.FilterObject;
import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.entity.SaleHis;
import com.cv.inv.api.entity.SaleHisDetail;
import com.cv.inv.api.service.SaleDetailService;
import com.cv.inv.api.service.SaleHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Lenovo
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
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-sale")
    public ResponseEntity<SaleHis> saveSale(@RequestBody SaleHis sale, HttpServletRequest request) {
        log.info("/save-sale");
        try {
            sale = shService.save(sale);
        } catch (Exception e) {
            log.error(String.format("saveSale %s", e.getMessage()));
        }
        //send message to service
        try {
            messageSender.sendMessage("SALE", sale.getVouNo());
        } catch (Exception e) {
            SaleHis sh = shService.findById(sale.getVouNo());
            sh.setIntgUpdStatus(null);
            shService.update(sh);
            log.error(String.format("sendMessage: SALE %s", e.getMessage()));
        }
        return ResponseEntity.ok(sale);
    }

    @PostMapping(path = "/get-sale")
    public ResponseEntity<List<SaleHis>> getSale(@RequestBody FilterObject filter) {
        log.info("/get-sale");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        List<SaleHis> listSale = shService.search(fromDate, toDate, cusCode, vouNo, userCode);
        return ResponseEntity.ok(listSale);
    }

    @DeleteMapping(path = "/delete-sale")
    public ResponseEntity<ReturnObject> deleteSale(@RequestParam String code) throws Exception {
        log.info("/delete-sale");
        shService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-sale")
    public ResponseEntity<SaleHis> findSale(@RequestParam String code) {
        log.info("/find-sale");
        SaleHis sh = shService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-sale-detail")
    public ResponseEntity<List<SaleHisDetail>> getSaleDetail(@RequestParam String vouNo) {
        log.info("/get-sale-detail");
        List<SaleHisDetail> listSD = sdService.search(vouNo);
        return ResponseEntity.ok(listSD);
    }
}
