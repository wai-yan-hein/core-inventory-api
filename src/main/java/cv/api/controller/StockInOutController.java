/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import cv.api.entity.StockInOutDetail;
import cv.api.service.ReportService;
import cv.api.service.StockInOutDetailService;
import cv.api.service.StockInOutService;
import cv.api.model.VStockIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/stockio")
@Slf4j
public class StockInOutController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private StockInOutService ioService;
    @Autowired
    private StockInOutDetailService iodService;
    @Autowired
    private ReportService reportService;

    @PostMapping(path = "/save-stockio")
    public ResponseEntity<StockInOut> saveStockIO(@RequestBody StockInOut stockio, HttpServletRequest request) throws Exception {
        stockio.setUpdatedDate(Util1.getTodayDate());
        stockio = ioService.save(stockio);
        return ResponseEntity.ok(stockio);
    }

    @PostMapping(path = "/get-stockio")
    public Flux<?> getStockIO(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String description = Util1.isNull(filter.getDescription(), "-");
        String vouStatus = Util1.isNull(filter.getVouStatus(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        List<VStockIO> listStockIO = reportService.getStockIOHistory(fromDate, toDate, vouStatus, vouNo, remark, description, userCode, stockCode, locCode, compCode, deptId, deleted);
        return Flux.fromIterable(listStockIO);
    }

    @PostMapping(path = "/delete-stockio")
    public ResponseEntity<ReturnObject> deleteStockIO(@RequestBody StockIOKey key) throws Exception {
        ioService.delete(key);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-stockio")
    public ResponseEntity<ReturnObject> restoreStockIO(@RequestBody StockIOKey key) throws Exception {
        ioService.restore(key);
        ro.setMessage("Restored.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-stockio")
    public ResponseEntity<StockInOut> findStockIO(@RequestBody StockIOKey key) {
        StockInOut sh = ioService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-stockio-detail")
    public Flux<?> getStockIODetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<StockInOutDetail> listSD = iodService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(listSD);
    }
}
