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
import com.cv.inv.api.entity.RetInHis;
import com.cv.inv.api.entity.RetInHisDetail;
import com.cv.inv.api.service.RetInDetailService;
import com.cv.inv.api.service.RetInService;
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
@RequestMapping("/retin")
@Slf4j
public class RetInController {

    @Autowired
    private RetInService riService;
    @Autowired
    private RetInDetailService rdService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-retin")
    public ResponseEntity<RetInHis> saveReturnIn(@RequestBody RetInHis retin, HttpServletRequest request) {
        log.info("/save-retin");
        try {
            retin = riService.save(retin);
        } catch (Exception e) {
            log.error(String.format("saveReturnIn: %s", e.getMessage()));
        }
        //send message to service
        try {
            messageSender.sendMessage("RETURN_IN", retin.getVouNo());
        } catch (Exception e) {
            RetInHis ri = riService.findById(retin.getVouNo());
            ri.setIntgUpdStatus(null);
            riService.update(ri);
            log.error(String.format("sendMessage: RETURN_IN %s", e.getMessage()));
        }
        return ResponseEntity.ok(retin);
    }

    @PostMapping(path = "/get-retin")
    public ResponseEntity<List<RetInHis>> getRI(@RequestBody FilterObject filter) {
        log.info("/get-retin");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        List<RetInHis> listRI = riService.search(fromDate, toDate, cusCode, vouNo, userCode);
        return ResponseEntity.ok(listRI);
    }

    @DeleteMapping(path = "/delete-retin")
    public ResponseEntity<ReturnObject> deleteRI(@RequestParam String code) throws Exception {
        log.info("/delete-retin");
        riService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-retin")
    public ResponseEntity<RetInHis> findRI(@RequestParam String code) {
        log.info("/find-retin");
        RetInHis sh = riService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-retin-detail")
    public ResponseEntity<List<RetInHisDetail>> getRIDetail(@RequestParam String vouNo) {
        log.info("/get-retin-detail");
        List<RetInHisDetail> listSD = rdService.search(vouNo);
        return ResponseEntity.ok(listSD);
    }
}
