/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.*;
import cv.api.entity.ReorderLevel;
import cv.api.entity.VStockBalance;
import cv.api.model.*;
import cv.api.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping(value = "/getSaleReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> getSaleReport(@RequestParam String vouNo,
                                              @RequestParam String compCode,
                                              @RequestParam Integer macId) throws Exception {
        String reportName = "SaleVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        createFilePath(exportPath);
        List<VSale> listVSale = reportService.getSaleVoucher(vouNo, compCode);
        return Flux.fromIterable(listVSale).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(value = "/getSaleByBatchReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Flux<VSale> getSaleByBatchReport(@RequestParam String vouNo,
                                                          @RequestParam String grnVouNo,
                                                          @RequestParam String compCode) {
        return Flux.fromIterable(reportService.getSaleByBatchReport(vouNo, grnVouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    private void createFilePath(String path) {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                log.info("Directory path created: " + parentDir.getAbsolutePath());
            } else {
                log.error("Failed to create directory path: " + parentDir.getAbsolutePath());
            }
        }
    }

    @GetMapping(value = "/getOrderReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody byte[] getOrderReport(@RequestParam String vouNo,
                                               @RequestParam String compCode,
                                               @RequestParam Integer macId) throws Exception {
        String reportName = "OrderVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VOrder> listVSale = reportService.getOrderVoucher(vouNo, compCode);
        Util1.writeJsonFile(listVSale, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @GetMapping(value = "/getPurchaseReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> getPurchaseReport(@RequestParam String vouNo, @RequestParam String compCode) throws Exception {
        List<VPurchase> listPur = reportService.getPurchaseVoucher(vouNo, compCode);
        return Flux.fromIterable(listPur);
    }

    @GetMapping(value = "/getGRNReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> getGRNReport(@RequestParam String vouNo, @RequestParam String compCode) throws Exception {
        List<VPurchase> listPur = reportService.getGRNVoucher(vouNo, compCode);
        return Flux.fromIterable(listPur);
    }

    @GetMapping(value = "/getPurWeightReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> getPurWeightReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam String batchNo) {
        List<VPurchase> list = reportService.getPurchaseByWeightVoucher(vouNo, Util1.isNull(batchNo, "-"), compCode);
        return Flux.fromIterable(list);
    }

    @GetMapping(value = "/getReturnInReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody byte[] getReturnInReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer macId) throws Exception {
        String reportName = "ReturnInVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnIn> listRI = reportService.getReturnInVoucher(vouNo, compCode);
        Util1.writeJsonFile(listRI, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @GetMapping(value = "/getTransferReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> getTransferReport(@RequestParam String vouNo,
                                     @RequestParam String compCode) {
        List<VTransfer> listRI = reportService.getTransferVoucher(vouNo, compCode);
        return Flux.fromIterable(listRI).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(value = "/getStockInOutVoucher", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<?> setStockInOutReport(@RequestParam String vouNo,
                                       @RequestParam String compCode) {
        List<VStockIO> listRI = reportService.getStockInOutVoucher(vouNo, compCode);
        return Flux.fromIterable(listRI).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(value = "/getReturnOutReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody byte[] getReturnOutReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer macId) throws Exception {
        String reportName = "ReturnOutVoucher";
        String exportPath = String.format("temp%s%s.json", File.separator, reportName + macId);
        List<VReturnOut> listRO = reportService.getReturnOutVoucher(vouNo, compCode);
        Util1.writeJsonFile(listRO, exportPath);
        return new FileInputStream(exportPath).readAllBytes();
    }

    @PostMapping(value = "/getReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ReturnObject> getReport(@RequestBody ReportFilter filter) {
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
                String batchNo = Util1.isNull(filter.getBatchNo(), "-");
                String projectNo = Util1.isAll(filter.getProjectNo());
                float creditAmt = filter.getCreditAmt();
                boolean calSale = filter.isCalSale();
                boolean calPur = filter.isCalPur();
                boolean calRI = filter.isCalRI();
                boolean calRO = filter.isCalRO();
                boolean calMill = filter.isCalMill();
                String fromDueDate = filter.getFromDueDate();
                String toDueDate = filter.getToDueDate();
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
                        List<VSale> saleByStock = reportService.getSaleByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                        Util1.writeJsonFile(saleByStock, exportPath);
                    }
                    case "SaleByStockWeightSummary" -> {
                        List<VSale> data = reportService.getSaleByStockWeightSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                        Util1.writeJsonFile(data, exportPath);
                    }
                    case "SaleByStockDetail" -> {
                        List<VSale> saleByStock = reportService.getSaleByStockDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                        Util1.writeJsonFile(saleByStock, exportPath);
                    }
                    case "OrderByStockSummary" -> {
                        List<VOrder> orderByStock = reportService.getOrderByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                        Util1.writeJsonFile(orderByStock, exportPath);
                    }
                    case "OrderByStockDetail" -> {
                        List<VOrder> orderByStock = reportService.getOrderByStockDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                        Util1.writeJsonFile(orderByStock, exportPath);
                    }
                    case "SaleByVoucherDetail", "SaleByVoucherDetailExcel" -> {
                        List<VSale> list = reportService.getSaleByVoucherDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleByVoucherSummary" -> {
                        List<VSale> list = reportService.getSaleByVoucherSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleByBatchDetail" -> {
                        List<VSale> list = reportService.getSaleByBatchDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleByProjectDetail" -> {
                        List<VSale> list = reportService.getSaleByProjectDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId, projectNo);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleByProjectSummary" -> {
                        List<VSale> list = reportService.getSaleByProjectSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode, deptId, projectNo);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "OrderByProjectDetail" -> {
                        List<VOrder> list = reportService.getOrderByProjectDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId, projectNo);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "OrderByProjectSummary" -> {
                        List<VOrder> list = reportService.getOrderByProjectSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode, deptId, projectNo);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "PurchaseBySupplierDetail" -> {
                        List<VPurchase> purchaseBySupplier = reportService.getPurchaseBySupplierDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode, macId);
                        Util1.writeJsonFile(purchaseBySupplier, exportPath);
                    }
                    case "PurchaseBySupplierSummary" -> {
                        List<VPurchase> purchaseBySupplier = reportService.getPurchaseBySupplierSummary(fromDate, toDate, typeCode, brandCode, catCode, stockCode, traderCode, compCode, deptId);
                        Util1.writeJsonFile(purchaseBySupplier, exportPath);
                    }
                    case "PurchaseByProjectDetail" -> {
                        List<VPurchase> purchaseByProject = reportService.getPurchaseByProjectDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode, macId, projectNo);
                        Util1.writeJsonFile(purchaseByProject, exportPath);
                    }
                    case "PurchaseByProjectSummary" -> {
                        List<VPurchase> purchaseByProject = reportService.getPurchaseByProjectSummary(fromDate, toDate, typeCode, brandCode, catCode, stockCode, traderCode, compCode, deptId, projectNo);
                        Util1.writeJsonFile(purchaseByProject, exportPath);
                    }
                    case "PurchaseByStockSummary" -> {
                        List<VPurchase> data = reportService.getPurchaseByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                        Util1.writeJsonFile(data, exportPath);
                    }
                    case "PurchaseByStockWeightSummary" -> {
                        List<VPurchase> data = reportService.getPurchaseByStockWeightSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                        Util1.writeJsonFile(data, exportPath);
                    }
                    case "PurchaseByStockDetail" -> {
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
                        List<VOpening> opening = reportService.getOpeningByLocation(typeCode, brandCode, catCode, stockCode, macId, compCode, deptId);
                        Util1.writeJsonFile(opening, exportPath);
                    }
                    case "OpeningByGroup" -> {
                        List<VOpening> opGroup = reportService.getOpeningByGroup(typeCode, stockCode, catCode, brandCode, macId, compCode, deptId);
                        Util1.writeJsonFile(opGroup, exportPath);
                    }
                    case "StockInOutSummary", "StockIOMovementSummary" -> {
                        List<ClosingBalance> listBalance = reportService.getStockInOutSummary(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockInOutDetail" -> {
                        reportService.calculateStockInOutDetail(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                        List<ClosingBalance> listBalance = reportService.getStockInOutDetail(typeCode, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockInOutSummaryByWeight" -> {
                        List<ClosingBalance> listBalance = reportService.getStockInOutSummaryByWeight(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO,calMill, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockInOutDetailByWeight" -> {
                        reportService.calculateStockInOutDetailByWeight(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO,calMill, compCode, deptId, macId);
                        List<ClosingBalance> listBalance = reportService.getStockInOutDetailByWeight(typeCode, compCode, deptId, macId);
                        Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockValue" -> {
                        List<StockValue> values = reportService.getStockValue(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
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
                    case "ProfitMarginByStock" -> {
                        List<VSale> values = reportService.getProfitMarginByStock(fromDate, toDate, curCode, stockCode, compCode, deptId);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "CustomerBalanceDetail" -> {
                        List<VSale> values = reportService.getCustomerBalanceDetail(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "CustomerBalanceSummary" -> {
                        List<VSale> values = reportService.getCustomerBalanceSummary(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode, creditAmt);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "SupplierBalanceDetail" -> {
                        List<VSale> values = reportService.getSupplierBalanceDetail(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "SupplierBalanceSummary" -> {
                        List<VSale> values = reportService.getSupplierBalanceSummary(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode, creditAmt);
                        Util1.writeJsonFile(values, exportPath);
                    }
                    case "SaleByDueDateSummary" -> {
                        List<VSale> list = reportService.getSaleByDueDate(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "SaleByDueDateDetail" -> {
                        List<VSale> list = reportService.getSaleByDueDateDetail(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "OrderByDueDateSummary" -> {
                        List<VOrder> list = reportService.getOrderByDueDate(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
                    }
                    case "OrderByDueDateDetail" -> {
                        List<VOrder> list = reportService.getOrderByDueDateDetail(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                        Util1.writeJsonFile(list, exportPath);
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
        return Mono.justOrEmpty(ro);
    }


    private boolean isValidReportFilter(ReportFilter filter, ReturnObject ro) {
        boolean status = true;
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        //String vouNo = filter.getVouNo();
        if (Util1.isNullOrEmpty(fromDate)) {
            status = false;
            ro.setMessage("Invalid From Date.");
        } else if (Util1.isNullOrEmpty(toDate)) {
            status = false;
            ro.setMessage("Invalid To Date.");
        }  else if (Util1.isNullOrEmpty(compCode)) {
            status = false;
            ro.setMessage("Invalid Company Id.");
        } else if (Util1.isNullOrEmpty(macId)) {
            status = false;
            ro.setMessage("Invalid Machine Id.");
        }
        return status;
    }


    @GetMapping(path = "/getPurchaseRecentPrice")
    public Mono<General> getPurchaseRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate,
                                                @RequestParam String unit, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode));
    }

    @GetMapping(path = "/getWeightLossRecentPrice")
    public Mono<General> getWeightLossRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getWeightLossRecentPrice(stockCode, vouDate, unit, compCode));
    }

    @GetMapping(path = "/getProductionRecentPrice")
    public Mono<General> getProductionRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getProductionRecentPrice(stockCode, vouDate, unit, compCode));
    }

    @GetMapping(path = "/getPurAvgPrice")
    public Mono<General> getPurAvgPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return Mono.just(reportService.getPurchaseAvgPrice(stockCode, vouDate, unit, compCode));
    }

    @GetMapping(path = "/getSaleRecentPrice")
    public Mono<General> getSaleRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getSaleRecentPrice(stockCode, vouDate, unit, compCode));
    }


    @GetMapping(path = "/getStockIORecentPrice")
    public Mono<General> getStockIORecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit) {
        return Mono.justOrEmpty(reportService.getStockIORecentPrice(stockCode, vouDate, unit));
    }

    @GetMapping(path = "/getStockBalance")
    public Flux<?> getStockBalance(@RequestParam String stockCode,
                                   @RequestParam boolean calSale, @RequestParam boolean calPur,
                                   @RequestParam boolean calRI, @RequestParam boolean calRO,
                                   @RequestParam String compCode, @RequestParam Integer deptId,
                                   @RequestParam Integer macId, @RequestParam boolean summary) {
        String opDate = reportService.getOpeningDate(compCode, deptId);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        List<VStockBalance> list = reportService.getStockBalance(opDate, clDate, "-", "-", "-",
                stockCode, calSale, calPur, calRI, calRO, "-", compCode, deptId, macId, summary);
        if (list.isEmpty()) {
            VStockBalance b = new VStockBalance();
            b.setLocationName("No Stock.");
            b.setUnitName("No Stock.");
        }
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockBalanceByWeight")
    public Flux<?> getStockBalanceByWeight(@RequestParam String stockCode,
                                           @RequestParam boolean calSale, @RequestParam boolean calPur,
                                           @RequestParam boolean calRI, @RequestParam boolean calRO,
                                           @RequestParam boolean calMill,
                                           @RequestParam String compCode, @RequestParam Integer deptId,
                                           @RequestParam Integer macId, @RequestParam boolean summary) {
        String opDate = reportService.getOpeningDate(compCode, deptId);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        List<VStockBalance> list = reportService.getStockBalanceByWeight(opDate, clDate, stockCode, calSale, calPur, calRI, calRO,calMill, compCode, macId, summary);
        if (list.isEmpty()) {
            VStockBalance b = new VStockBalance();
            b.setLocationName("No Stock.");
            b.setUnitName("No Stock.");
        }
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }


    @PostMapping(path = "/getReorderLevel")
    public Flux<?> getReorderLevel(@RequestBody ReportFilter filter) throws Exception {
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
        String opDate = reportService.getOpeningDate(compCode, deptId);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        List<ReorderLevel> reorderLevels = reportService.getReorderLevel(opDate, clDate, typeCode, catCode,
                brandCode, stockCode, calSale, calPur, calRI, calRO, locCode, compCode, deptId, macId);
        return Flux.fromIterable(reorderLevels).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getSmallQty")
    public Mono<?> getSmallQty(@RequestParam String stockCode, @RequestParam String unit, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Mono.justOrEmpty(reportService.getSmallestQty(stockCode, unit, compCode, deptId));
    }

    @PostMapping(path = "/getSaleSummaryByDepartment")
    public Flux<?> getSaleSummaryByDepartment(@RequestBody FilterObject filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return Flux.fromIterable(reportService.getSaleSummaryByDepartment(fromDate, toDate, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getOrderSummaryByDepartment")
    public Flux<?> getOrderSummaryByDepartment(@RequestBody FilterObject filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return Flux.fromIterable(reportService.getOrderSummaryByDepartment(fromDate, toDate, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getLandingReport")
    public Mono<?> getLandingReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getLandingReport(vouNo, compCode));
    }
}
