/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ClosingBalance;
import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.common.StockValue;
import cv.api.entity.*;
import cv.api.model.*;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author wai yan
 */
public interface ReportService {

    void insertTmp(List<String> listStr, Integer macId, String taleName);

    void executeSql(String... sql) throws Exception;

    ResultSet getResult(String sql) throws Exception;

    String getOpeningDate(String compCode, Integer deptIdF);

    void saveReportFilter(ReportFilter filter) throws Exception;

    List<VSale> getSaleVoucher(String vouNo) throws Exception;

    List<VOrder> getOrderVoucher(String vouNo) throws Exception;

    List<VPurchase> getPurchaseVoucher(String vouNo, String compCode) throws Exception;

    List<VSale> getSaleByCustomerDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception;


    List<VSale> getSaleBySaleManDetail(String fromDate, String toDate, String curCode, String smCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByCustomerSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId) throws Exception;

    List<VSale> getSaleByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    List<VOrder> getOrderByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    List<VSale> getSaleBySaleManSummary(String fromDate, String toDate, String typeCode,
                                        String catCode, String brandCode,
                                        String stockCode, String smCode, String compCode, Integer deptId) throws Exception;

    List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseByProjectDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId, String projectNo) throws Exception;

    List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate,
                                                 String typCode, String brandCode, String catCode,
                                                 String stockCode,
                                                 String traderCode, String compCode, Integer deptId) throws Exception;

    List<VPurchase> getPurchaseByProjectSummary(String fromDate, String toDate,
                                                String typCode, String brandCode, String catCode,
                                                String stockCode,
                                                String traderCode, String compCode, Integer deptId, String projectNo) throws Exception;

    List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VOrder> getOrderByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception;

    List<VOrder> getOrderByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleByVoucherDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleByVoucherSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleByBatchDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VSale> getSaleByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception;

    List<VOrder> getOrderByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception;

    List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId);

    General getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode, Integer deptId);

    General getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId);

    General getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId);

    General getSaleRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getStockIORecentPrice(String stockCode, String purDate, String unit);

    List<VStockBalance> getStockBalance(String typeCode, String catCode, String brandCode, String stockCode,
                                        boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                        String locCode, String compCode, Integer deptId, Integer macId, boolean summary);

    List<ClosingBalance> getClosingStock(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<ReorderLevel> getReorderLevel(String typeCode, String catCode, String brandCode, String stockCode,
                                       boolean calSale, boolean calPur, boolean calRI, boolean calRo,
                                       String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception;

    List<General> getTopSaleByCustomer(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleByStock(String fromDate, String toDate, String typeCode, String brandCode, String catCode, String compCode, Integer deptId) throws Exception;

    List<ClosingBalance> getClosingStockDetail(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                              String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) throws Exception;

    void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus,
                                   boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutDetail(String typeCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<StockValue> getStockValue(String opDate, String fromDate, String toDate, String typeCode, String catCode,
                                   String brandCode, String stockCode, String vouStatus,
                                   boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                   String compCode, Integer deptId, Integer macId) throws Exception;

    List<VOpening> getOpeningByLocation(String typeCode, String brandCode, String catCode, String stockCode, Integer macId, String compCode, Integer deptId) throws Exception;

    List<VOpening> getOpeningByGroup(String typeCode, String stockCode, String catCode, String brandCode, Integer macId, String compCode, Integer deptId) throws Exception;

    List<VStockIO> getStockIODetailByVoucherType(String vouType, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VStockIO> getStockIOHistory(String fromDate, String toDate, String vouStatus, String vouNo,
                                     String remark, String desp, String userCode, String stockCode,
                                     String locCode, String compCode, Integer deptId, String deleted) throws Exception;

    List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
                               String remark, String reference, String userCode, String stockCode, String locCode,
                               String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
                               String projectNo, String curCode);

    List<VOrder> getOrderHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
                                 String remark, String reference, String userCode, String stockCode, String locCode,
                                 String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
                                 String projectNo, String curCode);

    List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference,
                                       String userCode, String stockCode, String locCode, String compCode,
                                       Integer deptId, String deleted, String projectNo, String curCode) throws Exception;

    List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                       String userCode, String stockCode, String locCode, String compCode,
                                       Integer deptId, String deleted, String projectNo, String curCode) throws Exception;

    List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                         String userCode, String stockCode, String locCode,
                                         String compCode, Integer deptId, String deleted,
                                         String projectNo, String curCode) throws Exception;

    List<OPHis> getOpeningHistory(String fromDate, String toDate, String vouNo, String remark,
                                  String userCode, String stockCode, String locCode, String compCode,
                                  Integer deptId, String curCode) throws Exception;

    List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark,
                                       String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted) throws Exception;

    List<WeightLossHis> getWeightLossHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String stockCode, String locCode, String compCode, Integer deptId, String deleted);

    List<VSale> getSalePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchasePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId);

    List<String> isStockExist(String stockCode, String compCode);

    List<String> isTraderExist(String traderCode, String compCode);

    List<VReturnIn> getReturnInVoucher(String vouNo, String compCode);

    List<VReturnOut> getReturnOutVoucher(String vouNo, String compCode);

    List<VStockIO> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<GRN> getGRNHistory(String fromDate, String toDate, String batchNo, String traderCode, String vouNo,
                            String remark, String userCode, String stockCode, String locCode,
                            String compCode, Integer deptId, String deleted, String close, boolean orderByBatch);

    List<VPurchase> getPurchaseByWeightVoucher(String vouNo, String batchNo, String compCode);
}