/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisDetail;
import cv.api.inv.entity.TransferHisKey;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.TransferHisDetailService;
import cv.api.inv.service.TransferHisService;
import cv.api.inv.view.VTransfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/transfer")
@Slf4j
public class TransferController {

    @Autowired
    private TransferHisService thService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TransferHisDetailService detailService;

    @PostMapping(path = "/save-transfer")
    public ResponseEntity<TransferHis> saveTransfer(@RequestBody TransferHis transferHis) {
        transferHis = thService.save(transferHis);
        return ResponseEntity.ok(transferHis);
    }

    @PostMapping(path = "/get-transfer")
    public ResponseEntity<List<VTransfer>> getTransfer(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String locCodeTo = Util1.isNull(filter.getLocCodeTo(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        List<VTransfer> listStockIO = reportService.getTransferHistory(fromDate, toDate, refNo,
                vouNo, remark, userCode,
                stockCode, locCode, locCodeTo, compCode, deptId, deleted);
        return ResponseEntity.ok(listStockIO);
    }

    @PostMapping(path = "/find-transfer")
    public ResponseEntity<TransferHis> findTransfer(@RequestBody TransferHisKey code) {
        TransferHis sh = thService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-transfer-detail")
    public ResponseEntity<List<TransferHisDetail>> getPurDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<TransferHisDetail> listSD = detailService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }

    @PostMapping(path = "/delete-transfer")
    public ResponseEntity<ReturnObject> deleteTransfer(@RequestBody TransferHisKey key) {
        thService.delete(key);
        ReturnObject ro = new ReturnObject();
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/restore-transfer")
    public ResponseEntity<ReturnObject> restoreTransfer(@RequestBody TransferHisKey key) {
        thService.restore(key);
        ReturnObject ro = new ReturnObject();
        ro.setMessage("Restored.");
        return ResponseEntity.ok(ro);
    }
}
