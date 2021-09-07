/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.service.PurHisService;
import com.cv.inv.api.service.RetInService;
import com.cv.inv.api.service.RetOutService;
import com.cv.inv.api.service.SaleHisService;
import com.cv.inv.api.view.VPurchase;
import com.cv.inv.api.view.VReturnIn;
import com.cv.inv.api.view.VReturnOut;
import com.cv.inv.api.view.VSale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author Lenovo
 */
@RestController
@RequestMapping("/report")
@Slf4j
@AllArgsConstructor
public class ReportController {

    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private PurHisService purHisService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;

    @GetMapping(
            value = "/get-sale-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getSaleReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        log.info("getSaleReport is called.");
        String reportName = "SaleVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VSale> listVSale = saleHisService.search(vouNo);
        Util1.writeJsonFile(listVSale, exportPath);
        return IOUtils.toByteArray(new FileInputStream(exportPath));
    }

    @GetMapping(
            value = "/get-purchase-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getPurchaseReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        log.info("getPurchaseReport is called.");
        String reportName = "PurchaseVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VPurchase> listPur = purHisService.search(vouNo);
        Util1.writeJsonFile(listPur, exportPath);
        return IOUtils.toByteArray(new FileInputStream(exportPath));
    }

    @GetMapping(
            value = "/get-returnIn-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getReturnInReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        log.info("getReturnInReport is called.");
        String reportName = "ReturnInVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnIn> listRI = retInService.search(vouNo);
        Util1.writeJsonFile(listRI, exportPath);
        return IOUtils.toByteArray(new FileInputStream(exportPath));
    }

    @GetMapping(
            value = "/get-returnOut-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getReturnOutReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        log.info("getReturnOutReport is called.");
        String reportName = "ReturnOutVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnOut> listRO = retOutService.search(vouNo);
        Util1.writeJsonFile(listRO, exportPath);
        return IOUtils.toByteArray(new FileInputStream(exportPath));
    }
}
