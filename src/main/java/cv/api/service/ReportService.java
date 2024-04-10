/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ClosingBalance;
import cv.api.common.General;
import cv.api.common.ReturnObject;
import cv.api.common.StockValue;
import cv.api.entity.*;
import cv.api.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author wai yan
 */
public interface ReportService {

    void insertTmp(List<String> listStr, Integer macId, String taleName, String warehouse);

    void executeSql(String... sql) throws Exception;

    ResultSet getResult(String sql) throws Exception;

    ResultSet getResult(String sql, Object... params) throws Exception;

    String getOpeningDate(String compCode, int tranSource);
    String getOpeningDateByLocation(String compCode,String locCode);


    List<VSale> getSaleVoucher(String vouNo, String compCode) throws Exception;

    List<VOrder> getOrderVoucher(String vouNo, String compCode) throws Exception;

    List<VPurchase> getPurchaseVoucher(String vouNo, String compCode) throws Exception;

    List<VPurchase> getGRNVoucher(String vouNo, String compCode) throws Exception;

    Mono<ReturnObject> getSaleByCustomerDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception;


    Mono<ReturnObject> getSaleBySaleManDetail(String fromDate, String toDate, String curCode, String smCode, String stockCode, String compCode, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByCustomerSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId) throws Exception;

    Mono<ReturnObject> getSaleByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    Mono<ReturnObject> getOrderByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    Mono<ReturnObject> getSaleBySaleManSummary(String fromDate, String toDate, String typeCode,
                                        String catCode, String brandCode,
                                        String stockCode, String smCode, String compCode, Integer deptId) throws Exception;

    Mono<ReturnObject> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception;

    Mono<ReturnObject> getPurchaseByProjectDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId, String projectNo) throws Exception;

    Mono<ReturnObject> getPurchaseBySupplierSummary(String fromDate, String toDate,
                                                 String typCode, String brandCode, String catCode,
                                                 String stockCode,
                                                 String traderCode, String compCode, Integer deptId) throws Exception;

    Mono<ReturnObject> getPurchaseByProjectSummary(String fromDate, String toDate,
                                                String typCode, String brandCode, String catCode,
                                                String stockCode,
                                                String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    Mono<ReturnObject> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getOrderByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception;

    Mono<ReturnObject>  getOrderByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByVoucherDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByVoucherSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByBatchDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getSaleByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception;

    Mono<ReturnObject> getOrderByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception;

    Mono<ReturnObject> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId, String locCode) throws Exception;

    Mono<ReturnObject> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<ReturnObject> getPurchaseByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    Mono<General> getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode);

    Mono<General> getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode);

    Mono<General> getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode);

    Mono<General> getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode);

    Mono<General> getSaleRecentPrice(String stockCode, String purDate, String unit, String compCode);

    Mono<General> getStockIORecentPrice(String stockCode, String purDate, String unit);
    Mono<General> getWeightAvgPrice(String stockCode,String locCode, String compCode);


    List<VStockBalance> getStockBalance(String opDate, String clDate, String typeCode, String catCode, String brandCode, String stockCode,
                                        boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                        String locCode, String compCode, Integer deptId, Integer macId, boolean summary);

    List<VStockBalance> getStockBalanceByWeight(String opDate, String clDate, String stockCode,
                                                boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                                boolean calMill,
                                                String compCode, Integer macId, boolean summary);


    List<ClosingBalance> getClosingStock(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<ReorderLevel> getReorderLevel(String opDate, String clDate, String typeCode, String catCode, String brandCode, String stockCode,
                                       boolean calSale, boolean calPur, boolean calRI, boolean calRo,
                                       String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception;

    Mono<ReturnObject> getTopSaleByCustomer(String fromDate, String toDate, Integer deptId, String compCode) throws Exception;

    List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception;

    Mono<ReturnObject> getTopSaleByStock(String fromDate, String toDate, String typeCode, String brandCode, String catCode, String compCode, Integer deptId);


    List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                              String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) throws Exception;

    void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus,
                                   boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) throws Exception;

    void calculateStockInOutDetailByWeight(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus,
                                           boolean calSale, boolean calPur, boolean calRI, boolean calRO, boolean calMill, String compCode, Integer deptId, Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutDetail(String typeCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutDetailByWeight(String typeCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<StockValue> getStockValue(String opDate, String fromDate, String toDate, String typeCode, String catCode,
                                   String brandCode, String stockCode, String vouStatus,
                                   boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                   String compCode, Integer deptId, Integer macId) throws Exception;

    List<VOpening> getOpeningByLocation(String typeCode, String brandCode, String catCode, String stockCode, Integer macId, String compCode, Integer deptId) throws Exception;

    List<VOpening> getOpeningByGroup(String typeCode, String stockCode, String catCode, String brandCode, Integer macId, String compCode, Integer deptId) throws Exception;

    List<VStockIO> getStockIODetailByVoucherType(String vouType, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;


    List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark,
                                       String userCode, String stockCode, String locCode, String compCode, Integer deptId,
                                       String deleted, String traderCode) throws Exception;

    List<WeightLossHis> getWeightLossHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String stockCode, String locCode, String compCode, Integer deptId, String deleted);

    List<VSale> getSalePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchasePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId);

    Flux<General> isStockExist(String stockCode, String compCode);




    List<VStockIO> getStockInOutVoucher(String vouNo, String compCode);


    List<VStockIO> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<GRN> getGRNHistory(String fromDate, String toDate, String batchNo, String traderCode, String vouNo,
                            String remark, String userCode, String stockCode, String locCode,
                            String compCode, Integer deptId, String deleted, String close, boolean orderByBatch);

    List<VPurchase> getPurchaseByWeightVoucher(String vouNo, String batchNo, String compCode);


    List<VSale> getCustomerBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                          String batchNo, String projectNo, String locCode, double creditAmt);

    List<VSale> getSupplierBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                          String batchNo, String projectNo, String locCode, double creditAmt);

    List<VSale> getCustomerBalanceDetail(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                         String batchNo, String projectNo, String locCode);

    List<VSale> getSupplierBalanceDetail(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                         String batchNo, String projectNo, String locCode);

    List<VSale> getProfitMarginByStock(String fromDate, String toDate, String curCode, String stockCode, String compCode,
                                       Integer deptId) throws Exception;

    List<VSale> getSaleByDueDate(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                 String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleByDueDateDetail(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                       String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VOrder> getOrderByDueDate(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                   String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VOrder> getOrderByDueDateDetail(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                         String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleSummaryByDepartment(String fromDate, String toDate, String compCode);

    List<VOrder> getOrderSummaryByDepartment(String fromDate, String toDate, String compCode);

    List<VSale> getSaleByBatchReport(String vouNo, String grnVouNo, String compCode);

    List<ClosingBalance> getStockInOutSummaryByWeight(String opDate, String fromDate, String toDate,
                                                      String typeCode, String catCode, String brandCode,
                                                      String stockCode, String vouTypeCode,
                                                      boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                                      boolean calMill,
                                                      String compCode, Integer deptId, Integer macId);


    VLanding getLandingReport(String vouNo, String compCode);

    List<ClosingBalance> getStockPayableByTrader(String opDate, String fromDate, String toDate,
                                                 String traderCode, String stockCode, String compCode, int macId, boolean summary);

    Mono<ReturnObject> getSaleByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<ClosingBalance> getStockPayableConsignor(String opDate, String fromDate, String toDate, String traderCode, String stockCode, String compCode, int macId, boolean summary);

    List<VPurchase> getPurchaseList(String fromDate, String toDate, String compCode, String stockCode,
                                    String groupCode, String catCode, String brandCode, String locCode, String labourGroupCode);

    List<VPurOrder> getPurOrderHistory(String fromDate, String toDate, String traderCode, String userCode, String stockCode,
                                       String vouNo, String remark, Integer deptId,
                                       boolean deleted, String compCode);
    List<MillingHis> getMillingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference,
                                       String userCode, String stockCode, String locCode, String compCode,
                                       Integer deptId, boolean deleted, String projectNo, String curCode, String jobNo) throws Exception;
}
