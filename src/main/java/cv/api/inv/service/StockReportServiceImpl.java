/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.StockReportDao;
import cv.api.inv.entity.StockReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockReportServiceImpl implements StockReportService {

    @Autowired
    private StockReportDao dao;

    @Override
    public StockReport save(StockReport report) {
        return dao.save(report);
    }

    @Override
    public List<StockReport> getReports() {
        return dao.getReports();
    }

    @Override
    public List<StockReport> findAll() {
        return dao.findAll();
    }

}
