/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.entity.SeqKey;
import com.cv.inv.api.entity.SeqTable;
import com.cv.inv.api.service.SeqTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
@RequestMapping("/voucher")
@Slf4j
public class VoucherController {

    @Autowired
    private SeqTableService seqService;
    private final ReturnObject ro = new ReturnObject();

    @GetMapping(path = "/get-vou-no")
    public ResponseEntity<ReturnObject> getVouNo(@RequestParam String macId,
            @RequestParam String option, @RequestParam String compCode) {
        log.info("/get-vou-no");
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyyyy");
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setMacId(Util1.getInteger(macId));
        key.setPeriod(period);
        key.setSeqOption(option);
        SeqTable seq = seqService.findById(key);
        int sequence = seq == null ? 1 : seq.getSeqNo();
        String vouNo = getVouNo(macId, sequence, period);
        ro.setMeesage(vouNo);
        return ResponseEntity.ok(ro);
    }

    private String getVouNo(String machineNo, int lastVouNo, String period) {
        String vouNo;
        if (machineNo.length() < 2) {
            int needToAdd = 2 - machineNo.length();

            for (int i = 0; i < needToAdd; i++) {
                machineNo = "0" + machineNo;
            }
        }
        String strVouNo = Integer.toString(lastVouNo);
        if (strVouNo.length() < 5) {
            int needToAdd = 5 - strVouNo.length();
            for (int i = 0; i < needToAdd; i++) {
                strVouNo = "0" + strVouNo;
            }
        }
        vouNo = machineNo + strVouNo + period;
        return vouNo;
    }
}
