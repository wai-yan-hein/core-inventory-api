/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.ClosingBalance;
import com.cv.inv.api.common.General;
import com.cv.inv.api.common.ReportFilter;
import com.cv.inv.api.entity.ReorderLevel;
import com.cv.inv.api.entity.VStockBalance;
import com.cv.inv.api.view.VPurchase;
import com.cv.inv.api.view.VSale;

import java.util.List;

/**
 * @author Lenovo
 */
public interface ReportService {
    void executeSql(String... sql) throws Exception;

    void saveReportFilter(ReportFilter filter) throws Exception;

    List<VSale> getSaleVoucher(String vouNo) throws Exception;

    List<VPurchase> getPurchaseVoucher(String vouNo) throws Exception;

    List<VSale> getSaleByCustomerDetail(String fromDate, String toDate, String curCode,
                                        String vouNo, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode,
                                                String vouNo, String compCode, Integer macId) throws Exception;

    List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode,
                                     String vouNo, String compCode, Integer macId) throws Exception;

    List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode,
                                             String vouNo, String compCode, Integer macId) throws Exception;

    General getPurchaseAvgPrice(String stockCode) throws Exception;

    List<VStockBalance> getStockBalance(String stockCode) throws Exception;

    List<ClosingBalance> getClosingStock(String fromDate, String toDate, Integer macId) throws Exception;

    List<ReorderLevel> getReorderLevel(String compCode) throws Exception;
    void generateReorder(String compCode) throws Exception;
}
