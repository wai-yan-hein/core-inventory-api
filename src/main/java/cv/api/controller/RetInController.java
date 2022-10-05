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
import cv.api.inv.entity.RetInHis;
import cv.api.inv.entity.RetInHisDetail;
import cv.api.inv.entity.RetInHisKey;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.RetInDetailService;
import cv.api.inv.service.RetInService;
import cv.api.inv.view.VReturnIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/retin")
@Slf4j
public class RetInController {

    @Autowired
    private RetInService riService;
    @Autowired
    private RetInDetailService rdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-retin")
    public ResponseEntity<RetInHis> saveReturnIn(@RequestBody RetInHis retin, HttpServletRequest request) {
        try {
            retin = riService.save(retin);
        } catch (Exception e) {
            log.error(String.format("saveReturnIn: %s", e.getMessage()));
        }
        //send message to service
        try {
            messageSender.sendMessage("RETURN_IN", retin.getKey().getVouNo());
        } catch (Exception e) {
            RetInHis ri = riService.findById(retin.getKey());
            ri.setIntgUpdStatus(null);
            riService.update(ri);
            log.error(String.format("sendMessage: RETURN_IN %s", e.getMessage()));
        }
        return ResponseEntity.ok(retin);
    }

    @PostMapping(path = "/get-retin")
    public ResponseEntity<List<VReturnIn>> getRI(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        List<VReturnIn> listRI = reportService.getReturnInHistory(fromDate, toDate, cusCode, vouNo, remark, userCode, stockCode, locCode, compCode);
        return ResponseEntity.ok(listRI);
    }

    @DeleteMapping(path = "/delete-retin")
    public ResponseEntity<ReturnObject> deleteRI(@RequestParam String code) throws Exception {
        riService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-retin")
    public ResponseEntity<RetInHis> findRI(@RequestBody RetInHisKey key) {
        RetInHis sh = riService.findById(key);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-retin-detail")
    public ResponseEntity<List<RetInHisDetail>> getRIDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<RetInHisDetail> listSD = rdService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(listSD);
    }
}
