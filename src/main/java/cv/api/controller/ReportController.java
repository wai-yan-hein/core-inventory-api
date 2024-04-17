/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.*;
import cv.api.entity.OPHis;
import cv.api.entity.VStockBalance;
import cv.api.model.*;
import cv.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/report")
@Slf4j
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ReportR2dbcService reportR2dbcService;
    private final StockReportService stockReportService;
    private final TransferService transferService;
    private final StockRelationService stockRelationService;
    private final LocationService locationService;

    @GetMapping(value = "/getSaleReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VSale> getSaleReport(@RequestParam String vouNo,
                                     @RequestParam String compCode,
                                     @RequestParam Integer macId) {
        return reportService.getSaleVoucher(vouNo, compCode);
    }

    @GetMapping(value = "/getSaleByBatchReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VSale> getSaleByBatchReport(@RequestParam String vouNo,
                                            @RequestParam String grnVouNo,
                                            @RequestParam String compCode) {
        return reportService.getSaleByBatchReport(vouNo, grnVouNo, compCode);
    }

    @GetMapping(value = "/getOrderReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VOrder> getOrderReport(@RequestParam String vouNo,
                                       @RequestParam String compCode,
                                       @RequestParam Integer macId) {
        return reportService.getOrderVoucher(vouNo, compCode);
    }

    @GetMapping(value = "/getPurchaseReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VPurchase> getPurchaseReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return reportService.getPurchaseVoucher(vouNo, compCode);
    }

    @GetMapping(value = "/getGRNReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VPurchase> getGRNReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return reportService.getGRNVoucher(vouNo, compCode);
    }

    @GetMapping(value = "/getPurWeightReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VPurchase> getPurWeightReport(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam String batchNo) {
        return reportService.getPurchaseByWeightVoucher(vouNo, Util1.isNull(batchNo, "-"), compCode);
    }


    @GetMapping(value = "/getTransferReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VTransfer> getTransferReport(@RequestParam String vouNo,
                                             @RequestParam String compCode) {
        return transferService.getTransferVoucher(vouNo, compCode);
    }

    @GetMapping(value = "/getStockInOutVoucher", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<VStockIO> setStockInOutReport(@RequestParam String vouNo,
                                              @RequestParam String compCode) {
        return reportService.getStockInOutVoucher(vouNo, compCode);
    }


    @PostMapping(value = "/getReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ReturnObject> getReport(@RequestBody ReportFilter filter) {
        ReturnObject ro = ReturnObject.builder().build();
        if (isValidReportFilter(filter, ro)) {
            String compCode = filter.getCompCode();
            int macId = filter.getMacId();
            int deptId = filter.getDeptId();
            String warehouse = Util1.isNull(filter.getWarehouseCode(), "-");
            List<String> listLocation = filter.getListLocation();
            return locationService.insertTmp(listLocation, compCode, macId, warehouse).flatMap(aBoolean -> {
                String locCode = Util1.isNull(filter.getLocCode(), "-");
                String opDate = reportService.getOpeningDateByLocation(compCode, locCode);
                String opPayableDate = reportService.getOpeningDate(compCode, OPHis.PAYABLE);
                String fromDate = filter.getFromDate();
                String toDate = filter.getToDate();
                String curCode = filter.getCurCode();
                String stockCode = Util1.isNull(filter.getStockCode(), "-");
                String brandCode = Util1.isNull(filter.getBrandCode(), "-");
                String catCode = Util1.isNull(filter.getCatCode(), "-");
                String traderCode = Util1.isNull(filter.getTraderCode(), "-");
                String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
                String vouTypeCode = Util1.isNull(filter.getVouTypeCode(), "-");
                String smCode = Util1.isNull(filter.getSaleManCode(), "-");
                String batchNo = Util1.isNull(filter.getBatchNo(), "-");
                String projectNo = Util1.isAll(filter.getProjectNo());
                String labourGroupCode = Util1.isAll(filter.getLabourGroupCode());
                double creditAmt = filter.getCreditAmt();
                boolean calSale = filter.isCalSale();
                boolean calPur = filter.isCalPur();
                boolean calRI = filter.isCalRI();
                boolean calRO = filter.isCalRO();
                boolean calMill = filter.isCalMill();
                String fromDueDate = filter.getFromDueDate();
                String toDueDate = filter.getToDueDate();
                String reportName = filter.getReportName();
                log.info("op date : " + opDate);
                switch (reportName) {
                    case "SaleByCustomerDetail" -> {
                        return reportService.getSaleByCustomerDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode);
                    }
                    case "SaleByCustomerSummary" -> {
                        return reportService.getSaleByCustomerSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode);
                    }
                    case "SaleBySaleManDetail" -> {
                        return reportService.getSaleBySaleManDetail(fromDate, toDate, curCode, smCode, stockCode, compCode);
                    }
                    case "SaleBySaleManSummary" -> {
                        return reportService.getSaleBySaleManSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, smCode, compCode, deptId);
                    }
                    case "SaleByStockSummary" -> {
                        return reportService.getSaleByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                    }
                    case "SaleByStockWeightSummary" -> {
                        return reportService.getSaleByStockWeightSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                    }
                    case "SaleByStockDetail" -> {
                        return reportService.getSaleByStockDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                    }
                    case "OrderByStockSummary" -> {
                        return reportService.getOrderByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                    }
                    case "OrderByStockDetail" -> {
                        return reportService.getOrderByStockDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, macId);
                    }
                    case "SaleByVoucherDetail", "SaleByVoucherDetailExcel" -> {
                        return reportService.getSaleByVoucherDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "SaleByVoucherSummary" -> {
                        return reportService.getSaleByVoucherSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "SaleByBatchDetail" -> {
                        return reportService.getSaleByBatchDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "SaleByProjectDetail" -> {
                        return reportService.getSaleByProjectDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId, projectNo);
                    }
                    case "SaleByProjectSummary" -> {
                        return reportService.getSaleByProjectSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode, deptId, projectNo);
                    }
                    case "OrderByProjectDetail" -> {
                        return reportService.getOrderByProjectDetail(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId, projectNo);
                    }
                    case "OrderByProjectSummary" -> {
                        return reportService.getOrderByProjectSummary(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, compCode, deptId);
                    }
                    case "PurchaseBySupplierDetail" -> {
                        return reportService.getPurchaseBySupplierDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode);
                    }
                    case "PurchaseBySupplierSummary" -> {
                        return reportService.getPurchaseBySupplierSummary(fromDate, toDate, typeCode, brandCode, catCode, stockCode, traderCode, compCode, deptId);
                    }
                    case "PurchaseByProjectDetail" -> {
                        return reportService.getPurchaseByProjectDetail(fromDate, toDate, curCode, traderCode, stockCode, compCode, macId, projectNo);
                    }
                    case "PurchaseByProjectSummary" -> {
                        return reportService.getPurchaseByProjectSummary(fromDate, toDate, typeCode, brandCode, catCode, stockCode, traderCode, compCode, deptId);
                    }
                    case "PurchaseByStockSummary" -> {
                        return reportService.getPurchaseByStockSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                    }
                    case "PurchaseByStockWeightSummary" -> {
                        return reportService.getPurchaseByStockWeightSummary(fromDate, toDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, compCode, deptId, macId);
                    }
                    case "PurchaseByStockDetail" -> {
                        return reportService.getPurchaseByStockDetail(fromDate, toDate, curCode, typeCode, catCode, brandCode, stockCode, compCode, macId, locCode);
                    }
                    case "PurchaseList" -> {
                        return reportService.getPurchaseList(fromDate, toDate, compCode, stockCode,
                                typeCode, catCode, brandCode, locCode, labourGroupCode);
                    }
                    case "StockListByGroup" -> {
                        return reportService.getStockListByGroup(typeCode, compCode, macId);
                    }
                    case "TopSaleByCustomer" -> {
                        return reportService.getTopSaleByCustomer(fromDate, toDate, deptId, compCode);
                    }
                    case "TopSaleBySaleMan" -> {
                        return reportService.getTopSaleBySaleMan(fromDate, toDate, compCode);
                    }
                    case "TopSaleByStock" -> {
                        return reportService.getTopSaleByStock(fromDate, toDate, typeCode, brandCode, catCode, compCode, deptId);
                    }
                    case "OpeningByLocation" -> {
                        return reportService.getOpeningByLocation(typeCode, brandCode, catCode, stockCode, macId, compCode, deptId);
                    }
                    case "OpeningByGroup" -> {
                        return reportService.getOpeningByGroup(typeCode, stockCode, catCode, brandCode, macId, compCode, deptId);
                    }
                    case "StockInOutSummary", "StockIOMovementSummary" -> {
                        return stockRelationService.getStockInOutSummary(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                    }
                    case "StockInOutDetail" -> {
                        return stockRelationService.getStockInOutDetail(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                    }
//                    case "StockInOutSummaryByWeight" -> {
//                        List<ClosingBalance> listBalance = reportService.getStockInOutSummaryByWeight(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, calMill, compCode, deptId, macId);
//                        Util1.writeJsonFile(listBalance, exportPath);
//                    }
                    case "StockInOutQtySummary", "StockInOutQtySummaryByStock" -> {
                        filter.setOpDate(opDate);
                        return stockReportService.getStockInOutPaddy(filter, false);
                    }
                    case "StockInOutQtySummaryWetRice" -> {
                        filter.setReportType(2);
                        filter.setOpDate(opDate);
                        return stockReportService.getStockInOutPaddy(filter, false);
                    }
                    case "StockInOutQtyDetailWetRice", "StockInOutQtyBagDetail" -> {
                        filter.setReportType(2);
                        filter.setOpDate(opDate);
                        return stockReportService.getStockInOutPaddy(filter, true);
                    }
                    case "StockInOutQtyDetail" -> {
                        filter.setReportType(0);
                        filter.setOpDate(opDate);
                        return stockReportService.getStockInOutPaddy(filter, true);
                    }
                    case "StockInOutBagSummary" -> {
                        filter.setOpDate(opDate);
                        filter.setReportType(1);
                        return stockReportService.getStockInOutPaddy(filter, false);
                    }
                    case "StockInOutDetailByWeight" -> {
//                    reportService.calculateStockInOutDetailByWeight(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, calMill, compCode, deptId, macId);
//                    List<ClosingBalance> listBalance = reportService.getStockInOutDetailByWeight(typeCode, compCode, deptId, macId);
//                    Util1.writeJsonFile(listBalance, exportPath);
                    }
                    case "StockValue" -> {
                        return stockRelationService.getStockValue(opDate, fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouTypeCode, calSale, calPur, calRI, calRO, compCode, deptId, macId);
                    }
                    case "StockValueQty" -> {
                        return stockReportService.getStockValueRO(filter);
                    }
                    case "StockOutByVoucherTypeDetail" -> {
                        return reportService.getStockIODetailByVoucherType(vouTypeCode, fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                    }
                    case "StockInOutPriceCalender" -> {
                        return reportService.getStockIOPriceCalender(vouTypeCode, fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, deptId);
                    }
                    case "SalePriceCalender" -> {
                        return reportService.getSalePriceCalender(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                    }
                    case "PurchasePriceCalender" -> {
                        return reportService.getPurchasePriceCalender(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
                    }
                    case "ProductionOutputDetail" -> {
                        return reportService.getProcessOutputDetail(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                    }
                    case "ProductionOutputSummary" -> {
                        return reportService.getProcessOutputSummary(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                    }
                    case "ProductionUsageSummary" -> {
                        return reportService.getProcessUsageSummary(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                    }
                    case "ProductionUsageDetail" -> {
                        return reportService.getProcessUsageDetail(fromDate, toDate, vouTypeCode, typeCode, catCode, brandCode, stockCode, compCode, deptId, macId);
                    }
                    case "ProfitMarginByStock" -> {
                        return reportService.getProfitMarginByStock(fromDate, toDate, curCode, stockCode, compCode, deptId);
                    }
                    case "CustomerBalanceDetail" -> {
                        return reportService.getCustomerBalanceDetail(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode);
                    }
                    case "CustomerBalanceSummary" -> {
                        return reportService.getCustomerBalanceSummary(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode, creditAmt);
                    }
                    case "SupplierBalanceDetail" -> {
                        return reportService.getSupplierBalanceDetail(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode);
                    }
                    case "SupplierBalanceSummary" -> {
                        return reportService.getSupplierBalanceSummary(fromDate, toDate, compCode, curCode, traderCode, batchNo, projectNo, locCode, creditAmt);
                    }
                    case "SaleByDueDateSummary" -> {
                        return reportService.getSaleByDueDate(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "SaleByDueDateDetail" -> {
                        return reportService.getSaleByDueDateDetail(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "OrderByDueDateSummary" -> {
                        return reportService.getOrderByDueDate(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "OrderByDueDateDetail" -> {
                        return reportService.getOrderByDueDateDetail(fromDueDate, toDueDate, curCode, stockCode, typeCode, brandCode, catCode, locCode, batchNo, compCode, deptId, macId);
                    }
                    case "StockPayableCustomerSummary" -> {
                        //return  reportService.getStockPayableByTrader(opPayableDate, fromDate, toDate, traderCode, stockCode, compCode, macId, true);
                    }
                    case "StockPayableCustomerDetail" -> {
                        //return reportService.getStockPayableByTrader(opPayableDate, fromDate, toDate, traderCode, stockCode, compCode, macId, false);
                    }
                    case "StockPayableConsignorSummary" -> {
                        //return reportService.getStockPayableConsignor(opDate, fromDate, toDate, traderCode, stockCode, compCode, macId, true);
                    }
                    case "StockPayableConsignorDetail" -> {
                        //return reportService.getStockPayableConsignor(opDate, fromDate, toDate, traderCode, stockCode, compCode, macId, false);
                    }
                    case "TopPurchaseQty" -> {
                        return reportR2dbcService.getTopPurchase(fromDate, toDate, compCode, stockCode, typeCode, catCode, brandCode, locCode);
                    }
                    case "TransferSaleClosing" -> {
                        filter.setOpDate(opDate);
                        return stockReportService.getTransferSaleClosing(filter);
                    }
                    case "ConsignBalanceSummary" -> {
                        String opConsingDate = reportService.getOpeningDate(compCode, OPHis.CONSIGN);
                        filter.setOpDate(opConsingDate);
                        return stockReportService.getStockInOutConsign(filter);
                    }
                }
                ro.setMessage("Report Not Exists.");
                return Mono.just(ro);
            });
        }
        return Mono.just(ro);
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
        } else if (Util1.isNullOrEmpty(compCode)) {
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
        return reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode);
    }

    @GetMapping(path = "/getWeightLossRecentPrice")
    public Mono<General> getWeightLossRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return reportService.getWeightLossRecentPrice(stockCode, vouDate, unit, compCode);
    }

    @GetMapping(path = "/getProductionRecentPrice")
    public Mono<General> getProductionRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return reportService.getProductionRecentPrice(stockCode, vouDate, unit, compCode);
    }

    @GetMapping(path = "/getPurAvgPrice")
    public Mono<General> getPurAvgPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return reportService.getPurchaseAvgPrice(stockCode, vouDate, unit, compCode);
    }

    @GetMapping(path = "/getSaleRecentPrice")
    public Mono<General> getSaleRecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit, @RequestParam String compCode) {
        return reportService.getSaleRecentPrice(stockCode, vouDate, unit, compCode);
    }


    @GetMapping(path = "/getStockIORecentPrice")
    public Mono<General> getStockIORecentPrice(@RequestParam String stockCode, @RequestParam String vouDate, @RequestParam String unit) {
        return reportService.getStockIORecentPrice(stockCode, vouDate, unit);
    }

    @GetMapping(path = "/getWeightAvgPrice")
    public Mono<General> getWeightAvgPrice(@RequestParam String stockCode,
                                           @RequestParam String locCode,
                                           @RequestParam String compCode) {
        return reportService.getWeightAvgPrice(stockCode, locCode, compCode);
    }

    @GetMapping(path = "/getStockBalance")
    public Flux<VStockBalance> getStockBalance(@RequestParam String stockCode,
                                               @RequestParam boolean calSale, @RequestParam boolean calPur,
                                               @RequestParam boolean calRI, @RequestParam boolean calRO,
                                               @RequestParam String compCode, @RequestParam Integer deptId,
                                               @RequestParam Integer macId, @RequestParam boolean summary) {
        String opDate = reportService.getOpeningDate(compCode, OPHis.STOCK_OP);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        List<VStockBalance> list = reportService.getStockBalance(opDate, clDate, "-", "-", "-",
                stockCode, calSale, calPur, calRI, calRO, "-", compCode, deptId, macId, summary);
        if (list.isEmpty()) {
            VStockBalance b = VStockBalance.builder()
                    .locationName("No Stock")
                    .unitName("No Stock.").build();
            list.add(b);
        }
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/getStockBalanceQty")
    public Flux<ClosingBalance> getStockBalance(@RequestBody ReportFilter filter) {
        reportService.insertTmp(filter.getListLocation(), filter.getMacId(), "f_location", "-");
        String opDate = reportService.getOpeningDateByLocation(filter.getCompCode(), "-");
        filter.setOpDate(opDate);
        filter.setDeptId(0);
        return stockReportService.getStockBalance(filter);
    }

    @GetMapping(path = "/getStockBalanceByWeight")
    public Flux<?> getStockBalanceByWeight(@RequestParam String stockCode,
                                           @RequestParam boolean calSale, @RequestParam boolean calPur,
                                           @RequestParam boolean calRI, @RequestParam boolean calRO,
                                           @RequestParam boolean calMill,
                                           @RequestParam String compCode, @RequestParam Integer deptId,
                                           @RequestParam Integer macId, @RequestParam boolean summary) {
        String opDate = reportService.getOpeningDate(compCode, OPHis.STOCK_OP);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        List<VStockBalance> list = reportService.getStockBalanceByWeight(opDate, clDate, stockCode, calSale, calPur, calRI, calRO, calMill, compCode, macId, summary);
        if (list.isEmpty()) {
            VStockBalance b = VStockBalance.builder()
                    .locationName("No Stock")
                    .unitName("No Stock.").build();
            list.add(b);
        }
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }


    @PostMapping(path = "/getReorderLevel")
    public Flux<?> getReorderLevel(@RequestBody ReportFilter filter) {
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
        String opDate = reportService.getOpeningDate(compCode, OPHis.STOCK_OP);
        String clDate = Util1.toDateStr(Util1.getTodayDate(), "yyyy-MM-dd");
        return reportService.getReorderLevel(opDate, clDate, typeCode, catCode,
                brandCode, stockCode, calSale, calPur, calRI, calRO, locCode, compCode, deptId, macId);
    }

    @GetMapping(path = "/getSmallQty")
    public Mono<?> getSmallQty(@RequestParam String stockCode, @RequestParam String unit, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Mono.justOrEmpty(reportService.getSmallestQty(stockCode, unit, compCode, deptId));
    }

    @PostMapping(path = "/getSaleSummaryByDepartment")
    public Flux<VSale> getSaleSummaryByDepartment(@RequestBody ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return reportService.getSaleSummaryByDepartment(fromDate, toDate, compCode);
    }

    @PostMapping(path = "/getOrderSummaryByDepartment")
    public Flux<VOrder> getOrderSummaryByDepartment(@RequestBody ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        return reportService.getOrderSummaryByDepartment(fromDate, toDate, compCode);
    }

    @GetMapping(path = "/getLandingReport")
    public Mono<?> getLandingReport(@RequestParam String vouNo, @RequestParam String compCode) {
        return Mono.justOrEmpty(reportService.getLandingReport(vouNo, compCode));
    }
}
