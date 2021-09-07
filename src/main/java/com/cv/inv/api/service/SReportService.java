/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import java.util.Map;

/**
 *
 * @author Lenovo
 */
public interface SReportService {

    void generateStockBalance(String stockCode, String locId, String compCode, String macId);

    void generateSaleByStock(String stockCode, String regionCode, String macId);

    void reportViewer(String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters);

    void reportJsonViewer(String path, String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters);

    String genJsonFile(final String strSql) throws Exception;

    void generatePDF(String reportPath, String exportPath, String fontPath, Map<String, Object> param) throws Exception;
}
