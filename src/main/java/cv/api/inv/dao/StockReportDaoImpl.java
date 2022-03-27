/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockReport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockReportDaoImpl extends AbstractDao<Integer, StockReport> implements StockReportDao {

    @Override
    public StockReport save(StockReport report) {
        persist(report);
        return report;
    }

    @Override
    public List<StockReport> getReports() {
        String hsql = "select o from StockReport o";
        return findHSQL(hsql);
    }

    @Override
    public List<StockReport> findAll() {
        String hsql = "select o from StockReport o";
        return findHSQL(hsql);
    }

    
}
