/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.SeqKey;
import cv.api.inv.entity.SeqTable;
import cv.api.inv.service.SeqTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/voucher")
@Slf4j
public class VoucherController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private SeqTableService seqService;

    @GetMapping(path = "/get-vou-no")
    public ResponseEntity<ReturnObject> getVouNo(@RequestParam String macId,
                                                 @RequestParam String option, @RequestParam String compCode) {
        log.info("/get-vou-no");
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(Util1.getInteger(macId));
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable seq = seqService.findById(key);
        int sequence = seq == null ? 1 : seq.getSeqNo();
        String vouNo = getVouNo(macId, sequence, period);
        ro.setMessage(vouNo);
        return ResponseEntity.ok(ro);
    }

    private String getVouNo(String machineNo, int lastVouNo, String period) {
        String vouNo;
        if (machineNo.length() < 2) {
            int needToAdd = 2 - machineNo.length();

            StringBuilder machineNoBuilder = new StringBuilder(machineNo);
            for (int i = 0; i < needToAdd; i++) {
                machineNoBuilder.insert(0, "0");
            }
            machineNo = machineNoBuilder.toString();
        }
        String strVouNo = Integer.toString(lastVouNo);
        if (strVouNo.length() < 5) {
            int needToAdd = 5 - strVouNo.length();
            StringBuilder strVouNoBuilder = new StringBuilder(strVouNo);
            for (int i = 0; i < needToAdd; i++) {
                strVouNoBuilder.insert(0, "0");
            }
            strVouNo = strVouNoBuilder.toString();
        }
        vouNo = machineNo + strVouNo + period;
        return vouNo;
    }
}
