/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Repository
public class StockInOutDaoImpl extends AbstractDao<StockIOKey, StockInOut> implements StockInOutDao {

    @Override
    public StockInOut findById(StockIOKey id) {
        StockInOut byKey = getByKey(id);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public StockInOut save(StockInOut stock) {
        stock.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp,
                                   String vouNo, String userCode, String vouStatus) {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.endsWith("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!desp.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.description like '" + desp + "%'";
            } else {
                strFilter = strFilter + " and o.description like '" + desp + "%'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!vouStatus.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatus.code = '" + vouStatus + "'";
            } else {
                strFilter = strFilter + " and o.vouStatus.code = '" + vouStatus + "'";
            }
        }
        String strSql = "select o from StockInOut o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo desc";
        }
        return findHSQL(strSql);

    }

    @Override
    public void delete(StockIOKey key) {
        StockInOut io = findById(key);
        io.setDeleted(true);
        io.setUpdatedDate(LocalDateTime.now());
        updateEntity(io);
    }

    @Override
    public void restore(StockIOKey key) throws Exception {
        StockInOut io = findById(key);
        io.setDeleted(false);
        io.setUpdatedDate(LocalDateTime.now());
        updateEntity(io);
    }



    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_in_out";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getSyncDate();
    }



}
