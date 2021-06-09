/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockInOutDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockInOutDetailDaoImpl extends AbstractDao<Integer, StockInOutDetail> implements StockInOutDetailDao {

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        persist(stock);
        return stock;
    }

    @Override
    public List<StockInOutDetail> search(String fromDate, String toDate, String stockCode, String locId, String option, String remark) {
        String hsql = "select o from  StockInOutDetail o";
        String hFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                hFilter = hFilter + " and date(o.date) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) >= '" + fromDate + "'";
            } else {
                hFilter = hFilter + " and date(o.date) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "date(o.date) <= '" + toDate + "'";
            } else {
                hFilter = hFilter + " and date(o.date) <= '" + toDate + "'";
            }
        }
        if (!stockCode.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.stock.stockCode = '" + stockCode + "'";
            } else {
                hFilter = hFilter + " and o.stock.stockCode = '" + stockCode + "'";
            }
        }
        if (!locId.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.location.locId = '" + locId + "'";
            } else {
                hFilter = hFilter + " and o.location.locId = '" + locId + "'";
            }
        }
        if (!option.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.optionType = '" + option + "'";
            } else {
                hFilter = hFilter + " and o.optionType = '" + option + "'";
            }
        }
        if (!remark.equals("-")) {
            if (hFilter.isEmpty()) {
                hFilter = "o.remark = '" + remark + "'";
            } else {
                hFilter = hFilter + " and o.remark = '" + remark + "'";
            }
        }
        if (!hFilter.isEmpty()) {
            hsql = hsql + " where " + hFilter;
        }
        return findHSQL(hsql);
    }

    @Override
    public int delete(Integer id) {
        String hsql = "delete from StockInOut o where o.id = '" + id.toString() + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public List<StockInOutDetail> search(String batchCode) {
        String hsql = "select o from StockInOutDetail o where o.batchCode ='" + batchCode + "'";
        return findHSQL(hsql);

    }

}
