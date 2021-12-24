/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.*;
import com.cv.inv.api.entity.ReorderLevel;
import com.cv.inv.api.entity.VStockBalance;
import com.cv.inv.api.service.ReportService;
import com.cv.inv.api.service.RetInService;
import com.cv.inv.api.service.RetOutService;
import com.cv.inv.api.view.VPurchase;
import com.cv.inv.api.view.VReturnIn;
import com.cv.inv.api.view.VReturnOut;
import com.cv.inv.api.view.VSale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private ReportService reportService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-filter")
    public ResponseEntity<ReturnObject> saveOpening(@RequestBody ReportFilter filter,
                                                    HttpServletRequest request) {
        log.info("/save-filter");
        try {
            reportService.saveReportFilter(filter);
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
        }
        ro.setMessage("Saved Filter");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(
            value = "/get-sale-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getSaleReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        log.info("getSaleReport is called.");
        String reportName = "SaleVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VSale> listVSale = reportService.getSaleVoucher(vouNo);
        Util1.writeJsonFile(listVSale, exportPath);
        return IOUtils.toByteArray(new FileInputStream(exportPath));
    }

    @GetMapping(
            value = "/get-purchase-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    byte[] getPurchaseReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        String reportName = "PurchaseVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VPurchase> listPur = reportService.getPurchaseVoucher(vouNo);
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

    @PostMapping(
            value = "/get-report",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ReturnObject getReport(@RequestBody ReportFilter filter) {
        String exportPath = String.format("temp%s%s.json", File.separator, filter.getReportName() + filter.getMacId());
        try {
            if (isValidReportFilter(filter, ro)) {
                String fromDate = filter.getFromDate();
                String toDate = filter.getToDate();
                String curCode = filter.getCurCode();
                String compCode = filter.getCompCode();
                Integer macId = filter.getMacId();
                String vouNo = filter.getVouNo();
                String reportName = filter.getReportName();
                switch (reportName) {
                    case "SaleByCustomerDetail" -> {
                        List<VSale> saleByCustomer = reportService.getSaleByCustomerDetail(fromDate,
                                toDate, curCode, Util1.isNull(vouNo, "-"), compCode, macId);
                        Util1.writeJsonFile(saleByCustomer, exportPath);
                    }
                    case "SaleByStockDetail" -> {
                        List<VSale> saleByStock = reportService.getSaleByStockDetail(fromDate,
                                toDate, curCode, Util1.isNull(vouNo, "-"), compCode, macId);
                        Util1.writeJsonFile(saleByStock, exportPath);
                    }
                    case "PurchaseBySupplierDetail" -> {
                        List<VPurchase> purchaseBySupplier = reportService.getPurchaseBySupplierDetail(fromDate,
                                toDate, curCode, Util1.isNull(vouNo, "-"), compCode, macId);
                        Util1.writeJsonFile(purchaseBySupplier, exportPath);
                    }
                    case "PurchaseByStockDetail" -> {
                        List<VPurchase> purchaseByStock = reportService.getPurchaseByStockDetail(fromDate,
                                toDate, curCode, Util1.isNull(vouNo, "-"), compCode, macId);
                        Util1.writeJsonFile(purchaseByStock, exportPath);
                    }
                    case "InventoryClosingSummary" -> {
                        List<ClosingBalance> balances = reportService.getClosingStock(fromDate, toDate, macId);
                        Util1.writeJsonFile(balances, exportPath);
                    }
                    default -> ro.setMessage("Report Not Exists.");
                }
                byte[] bytes = IOUtils.toByteArray(new FileInputStream(exportPath));
                ro.setFile(bytes);
                log.info("file exported.");
            }
        } catch (Exception e) {
            log.error(String.format("getReport : %s", e));
            ro.setMessage(e.getMessage());
        }
        return ro;
    }

    private boolean isValidReportFilter(ReportFilter filter, ReturnObject ro) {
        boolean status = true;
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String curCode = filter.getCurCode();
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        //String vouNo = filter.getVouNo();
        if (Util1.isNullOrEmpty(fromDate)) {
            status = false;
            ro.setMessage("Invalid From Date.");
        } else if (Util1.isNullOrEmpty(toDate)) {
            status = false;
            ro.setMessage("Invalid To Date.");
        } else if (Util1.isNullOrEmpty(curCode)) {
            status = false;
            ro.setMessage("Invalid Currency.");
        } else if (Util1.isNullOrEmpty(compCode)) {
            status = false;
            ro.setMessage("Invalid Company Id.");
        } else if (Util1.isNullOrEmpty(macId)) {
            status = false;
            ro.setMessage("Invalid Machine Id.");
        }
        return status;
    }

    @GetMapping(path = "/get-purchase-price")
    public ResponseEntity<General> getPurchaseAvgPrice(@RequestParam String stockCode,
                                                       HttpServletRequest request) {
        General g = new General();
        try {
            g = reportService.getPurchaseAvgPrice(stockCode);
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(g);
    }

    @GetMapping(path = "/get-stock-balance")
    public ResponseEntity<List<VStockBalance>> getStockBalance(@RequestParam String stockCode,
                                                               HttpServletRequest request) {
        List<VStockBalance> balances = new ArrayList<>();
        try {
            balances = reportService.getStockBalance(stockCode);
        } catch (Exception e) {
            log.error(String.format("getStockBalance: %s", e.getMessage()));
        }
        return ResponseEntity.ok(balances);
    }

    @GetMapping(path = "/get-reorder-level")
    public ResponseEntity<ReturnObject> getReorderLevel(@RequestParam String compCode) {
        try {
            reportService.generateReorder(compCode);
            List<ReorderLevel> reorderLevels = reportService.getReorderLevel(compCode);
            ro.setList(Arrays.asList(reorderLevels.toArray()));
        } catch (Exception e) {
            ro.setErrorMessage(e.getMessage());
            log.error(String.format("getReorderLevel: %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }
}
