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
import cv.api.inv.entity.RetOutHis;
import cv.api.inv.entity.RetOutHisDetail;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.RetOutDetailService;
import cv.api.inv.service.RetOutService;
import cv.api.inv.view.VReturnOut;
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
@RequestMapping("/retout")
@Slf4j
public class RetOutController {

    @Autowired
    private RetOutService roService;
    @Autowired
    private RetOutDetailService rdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-retout")
    public ResponseEntity<RetOutHis> saveReturnOut(@RequestBody RetOutHis retout,
                                                   HttpServletRequest request) {
        try {
            retout = roService.save(retout);
        } catch (Exception e) {
            log.error(String.format("saveReturnOut: %s", e.getMessage()));
        }
        try {
            messageSender.sendMessage("RETURN_OUT", retout.getVouNo());
        } catch (Exception e) {
            RetOutHis rh = roService.findById(retout.getVouNo());
            rh.setIntgUpdStatus(null);
            roService.update(rh);
            log.error(String.format("sendMessage RETURN_OUT : %s", e.getMessage()));
        }
        return ResponseEntity.ok(retout);
    }

    @PostMapping(path = "/get-retout")
    public ResponseEntity<List<VReturnOut>> getRO(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(),"-");
        String compCode = filter.getCompCode();
        List<VReturnOut> listRO=reportService.getReturnOutHistory(fromDate,toDate,cusCode,vouNo,remark,userCode,stockCode,compCode);
        return ResponseEntity.ok(listRO);
    }

    @DeleteMapping(path = "/delete-retout")
    public ResponseEntity<ReturnObject> deleteRO(@RequestParam String code) throws Exception {
        roService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-retout")
    public ResponseEntity<RetOutHis> findRO(@RequestParam String code) {
        RetOutHis sh = roService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-retout-detail")
    public ResponseEntity<List<RetOutHisDetail>> getRODetail(@RequestParam String vouNo) {
        List<RetOutHisDetail> listSD = rdService.search(vouNo);
        return ResponseEntity.ok(listSD);
    }
}
