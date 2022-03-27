/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.ClosingBalance;
import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.common.StockValue;
import cv.api.inv.entity.ReorderLevel;
import cv.api.inv.entity.VStockBalance;
import cv.api.inv.view.*;

import java.util.List;

/**
 * @author wai yan
 */
public interface ReportService {
    void insertTmp(List<String> listStr, Integer macId, String taleName);

    void executeSql(String... sql) throws Exception;

    void saveReportFilter(ReportFilter filter) throws Exception;

    List<VSale> getSaleVoucher(String vouNo) throws Exception;

    List<VPurchase> getPurchaseVoucher(String vouNo) throws Exception;

    List<VSale> getSaleByCustomerDetail(String fromDate, String toDate, String curCode,
                                        String traderCode, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByCustomerSummary(String fromDate, String toDate, String curCode,
                                         String traderCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode,
                                                String traderCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate, String curCode,
                                                 String traderCode, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode,
                                     String stockCode, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByStockSummary(String fromDate, String toDate, String curCode,
                                      String stockCode, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode,
                                             String stockCode, String compCode, Integer macId) throws Exception;

    General getPurchaseAvgPrice(String stockCode) throws Exception;

    General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getSaleRecentPrice(String stockCode, String purDate, String unit, String compCode);

    General getStockIORecentPrice(String stockCode, String purDate, String unit);

    List<VStockBalance> getStockBalance(String stockCode, boolean relation, Integer macId) throws Exception;

    List<ClosingBalance> getClosingStock(String fromDate, String toDate,
                                         String typeCode, String catCode,
                                         String brandCode, String stockCode,
                                         String compCode, Integer macId) throws Exception;

    List<ReorderLevel> getReorderLevel(String compCode) throws Exception;

    void generateReorder(String compCode) throws Exception;

    List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception;

    List<General> getTopSaleByCustomer(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception;

    List<General> getTopSaleByStock(String fromDate, String toDate, String typeCode, String compCode) throws Exception;

    List<ClosingBalance> getClosingStockDetail(String fromDate, String toDate,
                                               String typeCode, String catCode,
                                               String brandCode, String stockCode,
                                               String compCode, Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate,
                                              String typeCode, String catCode,
                                              String brandCode, String stockCode, String compCode,
                                              Integer macId) throws Exception;

    void calculateStockInOutDetail(String opDate, String fromDate, String toDate,
                                   String typeCode, String catCode,
                                   String brandCode, String stockCode, String compCode,
                                   Integer macId) throws Exception;

    List<ClosingBalance> getStockInOutDetail(String typeCode, Integer macId) throws Exception;

    List<StockValue> getStockValue(String opDate, String fromDate, String toDate,
                                   String typeCode, String catCode,
                                   String brandCode, String stockCode, String compCode,
                                   Integer macId) throws Exception;

    List<VOpening> getOpeningByLocation(Integer macId, String compCode) throws Exception;

    List<VOpening> getOpeningByGroup(String typeCode, Integer macId, String compCode) throws Exception;

    List<VStockIO> getStockIODetailByVoucherType(String vouType, String fromDate, String toDate,
                                                 String typeCode, String catCode,
                                                 String brandCode, String stockCode, String compCode,
                                                 Integer macId) throws Exception;

    List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate,
                                           String typeCode, String catCode,
                                           String brandCode, String stockCode, String compCode,
                                           Integer macId) throws Exception;

    List<VStockIO> getStockIOHistory(String fromDate, String toDate,
                                     String vouStatus,
                                     String vouNo, String remark, String desp,
                                     String userCode, String stockCode, String compCode) throws Exception;

    List<VSale> getSaleHistory(String fromDate, String toDate,
                               String traderCode, String saleManCode,
                               String vouNo, String remark, String reference,
                               String userCode, String stockCode,
                               String compCode) throws Exception;

    List<VPurchase> getPurchaseHistory(String fromDate, String toDate,
                                       String traderCode,
                                       String vouNo, String remark, String reference,
                                       String userCode, String stockCode,
                                       String compCode) throws Exception;

    List<VReturnIn> getReturnInHistory(String fromDate, String toDate,
                                       String traderCode,
                                       String vouNo, String remark,
                                       String userCode, String stockCode,
                                       String compCode) throws Exception;

    List<VReturnOut> getReturnOutHistory(String fromDate, String toDate,
                                         String traderCode,
                                         String vouNo, String remark,
                                         String userCode, String stockCode,
                                         String compCode) throws Exception;
}
