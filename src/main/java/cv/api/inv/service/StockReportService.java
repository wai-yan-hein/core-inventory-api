/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockReport;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockReportService {

     StockReport save(StockReport report);

     List<StockReport> getReports();

     List<StockReport> findAll();
}
