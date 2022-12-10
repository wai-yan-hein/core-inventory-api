/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.WeightLossHis;
import cv.api.inv.entity.WeightLossHisKey;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.WeightLossDetailService;
import cv.api.inv.service.WeightLossService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/weight")
@Slf4j
public class WeightLossController {
    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private WeightLossService weightLossService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private WeightLossDetailService weightLossDetailService;

    @PostMapping(path = "/save-weight-loss")
    public ResponseEntity<?> saveStockIO(@RequestBody WeightLossHis w) {
        return ResponseEntity.ok(weightLossService.save(w));
    }

    @PostMapping(path = "/get-weight-loss")
    public ResponseEntity<?> getStockIO(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        return ResponseEntity.ok(reportService.getWeightLossHistory(fromDate, toDate, refNo, vouNo, remark, stockCode, locCode, compCode, deptId, deleted));
    }

    @PostMapping(path = "/delete-weight-loss")
    public ResponseEntity<ReturnObject> deleteStockIO(@RequestBody WeightLossHisKey key) {
        weightLossService.delete(key);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-weight-loss")
    public ResponseEntity<ReturnObject> restoreStockIO(@RequestBody WeightLossHisKey key) {
        weightLossService.restore(key);
        ro.setMessage("Restored.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-weight-loss")
    public ResponseEntity<?> findStockIO(@RequestBody WeightLossHisKey key) {
        return ResponseEntity.ok(weightLossService.findById(key));
    }

    @GetMapping(path = "/get-weight-loss-detail")
    public ResponseEntity<?> getStockIODetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        return ResponseEntity.ok(weightLossDetailService.search(vouNo, compCode, deptId));
    }
}
