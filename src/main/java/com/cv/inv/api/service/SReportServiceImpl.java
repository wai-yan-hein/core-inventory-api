/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.SReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class SReportServiceImpl implements SReportService {

    @Autowired
    private SReportDao dao;

    @Override
    public void generateStockBalance(String stockCode, String locId, String compCode, String macId) {
        dao.generateStockBalance(stockCode, locId, compCode, macId);
    }

    @Override
    public void reportViewer(String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        dao.reportViewer(reportPath, filePath, fontPath, parameters);
    }

    @Override
    public void reportJsonViewer(String path, String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        dao.reportJsonViewer(path, reportPath, filePath, fontPath, parameters);
    }

    @Override
    public void generateSaleByStock(String stockCode, String regionCode, String macId) {
        dao.generateSaleByStock(stockCode, regionCode, macId);
    }

    @Override
    public String genJsonFile(final String strSql) throws Exception {
        return dao.genJsonFile(strSql);
    }

    @Override
    public void generatePDF(String reportPath, String exportPath, String fontPath, Map<String, Object> param) throws Exception {
        dao.generatePDF(reportPath, exportPath, fontPath, param);
    }

}
