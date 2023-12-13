/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ClosingBalance;
import cv.api.common.General;
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

    ResultSet getResult(String sql, Object... params) throws Exception;

    String getOpeningDate(String compCode, int tranSource);

    List<VSale> getSaleVoucher(String vouNo, String compCode) throws Exception;

    List<VOrder> getOrderVoucher(String vouNo, String compCode) throws Exception;

    List<VPurchase> getPurchaseVoucher(String vouNo, String compCode) throws Exception;

    List<VPurchase> getGRNVoucher(String vouNo, String compCode) throws Exception;

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

    List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId, String locCode) throws Exception;

    List<VPurchase> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<VPurchase> getPurchaseByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode);

    General getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode);

    General getSaleRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getStockIORecentPrice(String stockCode, String purDate, String unit);

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

    List<General> getTopSaleByCustomer(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleByStock(String fromDate, String toDate, String typeCode, String brandCode, String catCode, String compCode, Integer deptId) throws Exception;

    List<ClosingBalance> getClosingStockDetail(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

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

    List<VStockIO> getStockIOHistory(String fromDate, String toDate, String vouStatus, String vouNo,
                                     String remark, String desp, String userCode, String stockCode,
                                     String locCode, String compCode, Integer deptId, String deleted, String traderCode, String jobNo) throws Exception;

    List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
                               String remark, String reference, String userCode, String stockCode, String locCode,
                               String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
                               String projectNo, String curCode);

    List<VOrder> getOrderHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo,
                                 String remark, String reference, String userCode, String stockCode, String locCode,
                                 String compCode, Integer deptId, String deleted, String nullBatch, String batchNo,
                                 String projectNo, String curCode, String orderStatus);

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
                                  Integer deptId, String curCode, String deleted, int type, String traderCode) throws Exception;

    List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark,
                                       String userCode, String stockCode, String locCode, String compCode, Integer deptId,
                                       String deleted, String traderCode) throws Exception;

    List<WeightLossHis> getWeightLossHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String stockCode, String locCode, String compCode, Integer deptId, String deleted);

    List<VSale> getSalePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchasePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception;

    General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId);

    List<General> isStockExist(String stockCode, String compCode);

    List<General> isTraderExist(String traderCode, String compCode);

    List<VReturnIn> getReturnInVoucher(String vouNo, String compCode);

    List<VTransfer> getTransferVoucher(String vouNo, String compCode);

    List<VStockIO> getStockInOutVoucher(String vouNo, String compCode);

    List<VReturnOut> getReturnOutVoucher(String vouNo, String compCode);

    List<VStockIO> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<VStockIO> getProcessUsageDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId);

    List<GRN> getGRNHistory(String fromDate, String toDate, String batchNo, String traderCode, String vouNo,
                            String remark, String userCode, String stockCode, String locCode,
                            String compCode, Integer deptId, String deleted, String close, boolean orderByBatch);

    List<VPurchase> getPurchaseByWeightVoucher(String vouNo, String batchNo, String compCode);

    List<PaymentHisDetail> getTraderBalance(String traderCode, String tranOption, String compCode);

    List<VSale> getCustomerBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                          String batchNo, String projectNo, String locCode, float creditAmt);

    List<VSale> getSupplierBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode,
                                          String batchNo, String projectNo, String locCode, float creditAmt);

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

    List<VSale> getSaleByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception;

    List<ClosingBalance> getStockPayableConsignor(String opDate, String fromDate, String toDate, String traderCode, String stockCode, String compCode, int macId, boolean summary);

    List<VPurchase> getPurchaseList(String fromDate, String toDate, String compCode, String stockCode,
                                    String groupCode, String catCode, String brandCode, String locCode,String labourGroupCode);
    List<VPurchase> getTopPurchasePaddy(String fromDate,String toDate,String compCode,
                                        String stockCode,String groupCode,String catCode,
                                        String brandCode,String locCode);
    List<VStockIssueReceive> getStockIssueReceiveHistory(String fromDate, String toDate, String traderCode,String userCode,  String stockCode,
                                                         String vouNo, String remark, String locCode,Integer deptId,
                                                         boolean deleted, String compCode, int transSource);

    List<VPurOrder> getPurOrderHistory(String fromDate, String toDate, String traderCode,String userCode,  String stockCode,
                                                         String vouNo, String remark, String locCode,Integer deptId,
                                                         boolean deleted, String compCode, int transSource);
}
