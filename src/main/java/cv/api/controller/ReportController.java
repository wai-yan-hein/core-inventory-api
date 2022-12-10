/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.*;
import cv.api.inv.entity.ReorderLevel;
import cv.api.inv.entity.VStockBalance;
import cv.api.inv.service.ReportService;
import cv.api.inv.view.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/report")
@Slf4j
@AllArgsConstructor
public class ReportController {
    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private ReportService reportService;

    @PostMapping(path = "/save-filter")
    public ResponseEntity<ReturnObject> saveOpening(@RequestBody ReportFilter filter, HttpServletRequest request) {
        log.info("/save-filter");
        try {
            reportService.saveReportFilter(filter);
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
        }
        ro.setMessage("Saved Filter");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(value = "/get-sale-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    byte[] getSaleReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        String reportName = "SaleVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VSale> listVSale = reportService.getSaleVoucher(vouNo);
        Util1.writeJsonFile(listVSale, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @GetMapping(value = "/get-purchase-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    byte[] getPurchaseReport(@RequestParam String vouNo, @RequestParam Integer macId) throws Exception {
        String reportName = "PurchaseVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VPurchase> listPur = reportService.getPurchaseVoucher(vouNo);
        Util1.writeJsonFile(listPur, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @GetMapping(value = "/get-return-in-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    byte[] getReturnInReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer macId) throws Exception {
        String reportName = "ReturnInVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnIn> listRI = reportService.getReturnInVoucher(vouNo, compCode);
        Util1.writeJsonFile(listRI, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @GetMapping(value = "/get-return-out-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    byte[] getReturnOutReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer macId) throws Exception {
        String reportName = "ReturnOutVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnOut> listRO = reportService.getReturnOutVoucher(vouNo, compCode);
        Util1.writeJsonFile(listRO, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @PostMapping(value = "/get-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ReturnObject getReport(@RequestBody ReportFilter filter) {
        String exportPath = String.format("temp%s%s.json", File.separator, filter.getReportName() + filter.getMacId());
        try {
            if (isValidReportFilter(filter, ro)) {
                String compCode = filter.getCompCode();
                Integer deptId = filter.getDeptId();
                String opDate = reportService.getOpeningDate(compCode, deptId);
                String fromDate = filter.getFromDate();
                String toDate = filter.getToDate();
                String curCode = filter.getCurCode();
                Integer macId = filter.getMacId();
                String stockCode = Util1.isNull(filter.getStockCode(), "-");
                String brandCode = Util1.isNull(filter.getBrandCode(), "-");
                String catCode = Util1.isNull(filter.getCatCode(), "-");
                String traderCode = Util1.isNull(filter.getTraderCode(), "-");
                String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
                String vouTypeCode = Util1.isNull(filter.getVouTypeCode(), "-");
                String smCode = Util1.isNull(filter.getSaleManCode(), "-");
                String locCode = Util1.isNull(filter.getLocCode(), "-");
                boolean calSale = filter.isCalSale();
                boolean calPur = filter.isCalPur();
                boolean calRI = filter.isCalRI();
                boolean calRO = filter.isCalRO();
                String reportName = filter.getReportName();
                reportService.insertTmp(filter.getListLocation(), macId, "f_location");
                switch (reportName) {
                    case "SaleByCustomerDetail" -> {
                        List<VSale> saleByCustomer = reportService.getSaleByCustomerDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(saleByCustomer, exportPath);
                    }
                    case "SaleByCustomerSummary" -> {
                        List<VSale> list = reportService.getSaleByCustomerSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode, deptId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleBySaleManDetail" -> {
                        List<VSale> saleByCustomer = reportService.getSaleBySaleManDetail(fromDate, toDate, curCode, smCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(saleByCustomer, exportPath);
                    }
                    case "SaleBySaleManSummary" -> {
                        List<VSale> saleByCustomer = reportService.getSaleBySaleManSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, smCode, compCode, deptId);
                        Util1.writeJsonFile(saleByCustomer, exportPath);
                    }
                    case "SaleByStockSummary" -> {
                        List<VSale> saleByStock = reportService.getSaleByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                        Util1.writeJsonFile(saleByStock, exportPath);
                    }
                    case "SaleByStockDetail" -> {
                        List<VSale> saleByStock = reportService.getSaleByStockDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                        Util1.writeJsonFile(saleByStock, exportPath);
                    }
                    case "PurchaseBySupplierDetail" -> {
                        List<VPurchase> purchaseBySupplier = reportService.getPurchaseBySupplierDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(purchaseBySupplier, exportPath);
                    }
                    case "PurchaseBySupplierSummary" -> {
                        List<VPurchase> purchaseBySupplier = reportService.getPurchaseBySupplierSummary(fromDate, toDate, typeCode, brandCode, catCode, stockCode, traderCode, compCode, deptId);
                        Util1.writeJsonFile(purchaseBySupplier, exportPath);
                    }
                    case "PurchaseByStockSummary", "PurchaseByStockDetail" -> {
                        List<VPurchase> purchaseByStock = reportService.getPurchaseByStockDetail(fromDate, toDate, curCode, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(purchaseByStock, exportPath);
                    }

                    case "InventoryClosingSummary" -> {
                        List<ClosingBalance> balances = reportService.getClosingStock(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(balances, exportPath);
                    }
                    case "InventoryClosingDetail" -> {
                        List<ClosingBalance> c = reportService.getClosingStockDetail(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(c, exportPath);
                    }
                    case "StockListByGroup" -> {
                        List<General> generalList = reportService.getStockListByGroup(typeCode, compCode, macId);
                        Util1.writeJsonFile(generalList, exportPath);
                    }
                    case "TopSaleByCustomer" -> {
                        List<General> sale = reportService.getTopSaleByCustomer(fromDate, toDate, compCode);
                        Util1.writeJsonFile(sale, exportPath);
                    }
                    case "TopSaleBySaleMan" -> {
                        List<General> sale = reportService.getTopSaleBySaleMan(fromDate, toDate, compCode);
                        Util1.writeJsonFile(sale, exportPath);
                    }
                    case "TopSaleByStock" -> {
                        List<General> general = reportService.getTopSaleByStock(fromDate, toDate, typeCode, brandCode, catCode, compCode, deptId);
                        Util1.writeJsonFile(general, exportPath);
                    }
                    case "OpeningByLocation" -> {
                        List<VOpening> opening = reportService.getOpeningByLocation(typeCode, brandCode, catCode, stockCode, macId, compCode);
                        Util1.writeJsonFile(opening, exportPath);
                    }
                    case "OpeningByGroup" -> {
                        List<VOpening> opGroup = reportService.getOpeningByGroup(typeCode, stockCode, catCode, brandCode, macId, compCode);
                        Util1.writeJsonFile(opGroup, exportPath);
                    }
                    case "StockInOutSummary", "StockIOMovementSummary" -> {
                        List<ClosingBalance> listBalance = reportService.getStockInOutSummary(opDate, fromDate, toDate,
                                typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockInOutDetail" -> {
                        reportService.calculateStockInOutDetail(opDate, fromDate, toDate, typeCode, catCode, brandCode,
                                stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                        List<ClosingBalance> listBalance = reportService.getStockInOutDetail(typeCode, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockValue" -> {
                        List<StockValue> values = reportService.getStockValue(opDate, fromDate, toDate, typeCode, catCode,
                                brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "StockOutByVoucherTypeDetail" -> {
                        List<VStockIO> values = reportService.getStockIODetailByVoucherType(vouTypeCode, fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "StockInOutPriceCalender" -> {
                        List<VStockIO> values = reportService.getStockIOPriceCalender(vouTypeCode, fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, deptId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "SalePriceCalender" -> {
                        List<VSale> values = reportService.getSalePriceCalender(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "PurchasePriceCalender" -> {
                        List<VPurchase> values = reportService.getPurchasePriceCalender(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "ProductionOutputDetail" -> {
                        List<VStockIO> values = reportService.getProcessOutputDetail(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "ProductionOutputSummary" -> {
                        List<VStockIO> values = reportService.getProcessOutputSummary(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "ProductionUsageSummary" -> {
                        List<VStockIO> values = reportService.getProcessUsageSummary(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "ProductionUsageDetail" -> {
                        List<VStockIO> values = reportService.getProcessUsageDetail(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    default -> ro.setMessage("Report Not Exists.");
                }
                byte[] bytes = new FileInputStream(exportPath).readAllBytes();
                ro.setFile(bytes);
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


    @GetMapping(path = "/get-purchase-recent-price")
    public ResponseEntity<General> getPurchaseRecentPrice(@RequestParam String stockCode,
                                                          @RequestParam String vouDate,
                                                          @RequestParam String unit,
                                                          @RequestParam String compCode,
                                                          @RequestParam Integer deptId) {
        return ResponseEntity.ok(reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode, deptId));
    }

    @GetMapping(path = "/get-weight-loss-recent-price")
    public ResponseEntity<General> getWeightLossRecentPrice(@RequestParam String stockCode,
                                                            @RequestParam String vouDate,
                                                            @RequestParam String unit,
                                                            @RequestParam String compCode,
                                                            @RequestParam Integer deptId) {
        return ResponseEntity.ok(reportService.getWeightLossRecentPrice(stockCode, vouDate, unit, compCode, deptId));
    }

    @GetMapping(path = "/get-production-recent-price")
    public ResponseEntity<General> getProductionRecentPrice(@RequestParam String stockCode,
                                                            @RequestParam String vouDate,
                                                            @RequestParam String unit,
                                                            @RequestParam String compCode,
                                                            @RequestParam Integer deptId) {
        return ResponseEntity.ok(reportService.getProductionRecentPrice(stockCode, vouDate, unit, compCode, deptId));
    }

    @GetMapping(path = "/get-purchase-avg-price")
    public ResponseEntity<General> getPurAvgPrice(@RequestParam String stockCode,
                                                  @RequestParam String vouDate,
                                                  @RequestParam String unit,
                                                  @RequestParam String compCode,
                                                  @RequestParam Integer deptId) {
        return ResponseEntity.ok(reportService.getPurchaseAvgPrice(stockCode, vouDate, unit, compCode, deptId));
    }

    @GetMapping(path = "/get-sale-recent-price")
    public ResponseEntity<General> getSaleRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return ResponseEntity.ok(reportService.getSaleRecentPrice(stockCode, vouDate, unit, compCode));
    }


    @GetMapping(path = "/get-stock-io-recent-price")
    public ResponseEntity<General> getStockIORecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit) {
        return ResponseEntity.ok(reportService.getStockIORecentPrice(stockCode, vouDate, unit));
    }

    @GetMapping(path = "/get-stock-balance")
    public ResponseEntity<List<VStockBalance>> getStockBalance(@RequestParam String stockCode,
                                                               @RequestParam boolean calSale,
                                                               @RequestParam boolean calPur,
                                                               @RequestParam boolean calRI,
                                                               @RequestParam boolean calRO,
                                                               @RequestParam String compCode,
                                                               @RequestParam Integer deptId,
                                                               @RequestParam Integer macId) throws Exception {
        return ResponseEntity.ok(reportService.getStockBalance("-", "-", "-", stockCode, calSale, calPur, calRI, calRO, "-", compCode, deptId, macId));
    }

    @PostMapping(path = "/get-reorder-level")
    public ResponseEntity<List<ReorderLevel>> getReorderLevel(@RequestBody ReportFilter filter) throws Exception {
        String compCode = filter.getCompCode();
        String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
        String catCode = Util1.isNull(filter.getCatCode(), "-");
        String brandCode = Util1.isNull(filter.getBrandCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        Integer deptId = filter.getDeptId();
        Integer macId = filter.getMacId();
        boolean calSale = filter.isCalSale();
        boolean calPur = filter.isCalPur();
        boolean calRI = filter.isCalRI();
        boolean calRO = filter.isCalRO();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        List<ReorderLevel> reorderLevels = reportService.getReorderLevel(typeCode, catCode, brandCode, stockCode, calSale, calPur, calRI, calRO, locCode, compCode, deptId, macId);
        return ResponseEntity.ok(reorderLevels);
    }

    @GetMapping(path = "/get-smallest_qty")
    public ResponseEntity<Float> getSaleRecentPrice(@RequestParam String stockCode, @RequestParam String unit, @RequestParam String compCode, @RequestParam Integer deptId) {
        return ResponseEntity.ok(reportService.getSmallestQty(stockCode, unit, compCode, deptId));
    }
}
