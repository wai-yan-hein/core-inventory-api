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
import cv.api.inv.entity.PurHis;
import cv.api.inv.entity.PurHisDetail;
import cv.api.inv.service.PurHisDetailService;
import cv.api.inv.service.PurHisService;
import cv.api.inv.service.ReportService;
import cv.api.inv.view.VPurchase;
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
@RequestMapping("/pur")
@Slf4j
public class PurchaseController {

    @Autowired
    private PurHisService phService;
    @Autowired
    private PurHisDetailService pdService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-pur")
    public ResponseEntity<PurHis> savePurchase(@RequestBody PurHis pur, HttpServletRequest request) {
        log.info("/save-pur");
        try {
            pur = phService.save(pur);
        } catch (Exception e) {
            log.error(String.format("savePurchase: %s", e.getMessage()));
        }
        //send message to service
        try {
            messageSender.sendMessage("PURCHASE", pur.getVouNo());
        } catch (Exception e) {
            PurHis ph = phService.findById(pur.getVouNo());
            ph.setIntgUpdStatus(null);
            phService.update(ph);
            log.error(String.format("sendMessage: PURCHASE %s", e.getMessage()));
        }
        return ResponseEntity.ok(pur);
    }

    @PostMapping(path = "/get-pur")
    public ResponseEntity<List<VPurchase>> getPur(@RequestBody FilterObject filter) throws Exception {
        log.info("/get-pur");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String ref = Util1.isNull(filter.getReference(),"-");
        String compCode = filter.getCompCode();
        List<VPurchase> listPur = reportService.getPurchaseHistory(fromDate, toDate, cusCode, vouNo, remark,ref, userCode, stockCode, compCode);
        return ResponseEntity.ok(listPur);
    }

    @DeleteMapping(path = "/delete-pur")
    public ResponseEntity<ReturnObject> deletePur(@RequestParam String code) throws Exception {
        log.info("/delete-pur");
        phService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-pur")
    public ResponseEntity<PurHis> findPur(@RequestParam String code) {
        log.info("/find-pur");
        PurHis sh = phService.findById(code);
        return ResponseEntity.ok(sh);
    }

    @GetMapping(path = "/get-pur-detail")
    public ResponseEntity<List<PurHisDetail>> getPurDetail(@RequestParam String vouNo) {
        log.info("/get-pur-detail");
        List<PurHisDetail> listSD = pdService.search(vouNo);
        return ResponseEntity.ok(listSD);
    }
}
