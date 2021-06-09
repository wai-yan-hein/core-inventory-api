/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockInOut;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockInOutDaoImpl extends AbstractDao<String, StockInOut> implements StockInOutDao {

    @Override
    public StockInOut findById(String id) {
        return getByKey(id);
    }

    @Override
    public StockInOut save(StockInOut stock) {
        persist(stock);
        return stock;
    }

    @Override
    public List<StockInOut> search(String batchCode, String fromDate, String toDate, String desp, String remark) {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.tranDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.tranDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.tranDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.tranDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.tranDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.tranDate) <= '" + toDate + "'";
            }
        }
        if (!batchCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.batchCode = '" + batchCode + "'";
            } else {
                strFilter = strFilter + " and o.batchCode = '" + batchCode + "'";
            }
        }
        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description = '" + desp + "'";
            } else {
                strFilter = strFilter + " and o.description = '" + desp + "'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }
        String strSql = "select o from StockInOut o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + "and o.deleted is not true order by date(o.tranDate) desc, o.batchCode desc";
        }

        return findHSQL(strSql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockInOut o where o.batchCode '" + id + "'";
        return execUpdateOrDelete(hsql);

    }

}
