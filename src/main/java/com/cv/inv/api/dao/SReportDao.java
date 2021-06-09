/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import java.util.Map;

/**
 *
 * @author Lenovo
 */
public interface SReportDao {

    public void generateStockBalance(String stockCode, String locId, String compCode, String macId);

    public void generateSaleByStock(String stockCode, String regionCode, String macId);

    public void reportViewer(String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters);

    public void reportJsonViewer(String path, String reportPath, String filePath, String fontPath,
            Map<String, Object> parameters);

    public String genJsonFile(final String strSql) throws Exception;
}
