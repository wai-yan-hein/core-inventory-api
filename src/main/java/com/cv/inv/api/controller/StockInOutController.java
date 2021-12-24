/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.FilterObject;
import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.entity.StockInOut;
import com.cv.inv.api.entity.StockInOutDetail;
import com.cv.inv.api.service.StockInOutDetailService;
import com.cv.inv.api.service.StockInOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author Lenovo
 */
@RestController
@RequestMapping("/stockio")
@Slf4j
public class StockInOutController {

    @Autowired
    private StockInOutService ioService;
    @Autowired
    private StockInOutDetailService iodService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-stockio")
    public ResponseEntity<StockInOut> saveStockIO(@RequestBody StockInOut stockio, HttpServletRequest request) throws Exception {
        log.info("/save-stockio");
        stockio = ioService.save(stockio);
        return ResponseEntity.ok(stockio);
    }

    @PostMapping(path = "/get-stockio")
    public ResponseEntity<List<StockInOut>> getStockIO(@RequestBody FilterObject filter) {
        log.info("/get-stockio");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String description = Util1.isNull(filter.getDescription(), "-");
        List<StockInOut> listStockIO = ioService.search(fromDate, toDate, remark, description, vouNo, userCode);
        return ResponseEntity.ok(listStockIO);
    }

    @DeleteMapping(path = "/delete-stockio")
    public ResponseEntity<ReturnObject> deleteStockIO(@RequestParam String code) throws Exception {
        log.info("/delete-stockio");
        ioService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-stockio")
    public ResponseEntity<StockInOut> findStockIO(@RequestParam String code) {
        log.info("/find-stockio");
        StockInOut sh = ioService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-stockio-detail")
    public ResponseEntity<List<StockInOutDetail>> getStockIODetail(@RequestParam String vouNo) {
        log.info("/get-stockio-detail");
        List<StockInOutDetail> listSD = iodService.search(vouNo);
        return ResponseEntity.ok(listSD);
    }
}
