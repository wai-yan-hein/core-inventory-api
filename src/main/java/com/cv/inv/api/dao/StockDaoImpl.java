/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Stock;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockDaoImpl extends AbstractDao<String, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        persist(stock);
        return stock;
    }

    @Override
    public Stock findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        String hsql = "select o from Stock o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from Stock o where o.stock_code = '" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public Object getMax(String sql) {
        return getAggregate(sql);
    }

    @Override
    public List<Stock> findActiveStock() {
        String hsql = "select o from Stock o where o.isActive is true";
        return findHSQL(hsql);

    }

    @Override
    public List<Stock> search(String saleInvId) {
        String strFilter = "";
        if (!saleInvId.equals("-")) {
            strFilter = "v.stockType = '" + saleInvId + "'";
        }
        String strSql = "select v from Stock v";

        List<Stock> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public List<Stock> searchC(String saleInvId) {
        String strFilter = "";
        if (!saleInvId.equals("-")) {
            strFilter = "v.category = '" + saleInvId + "'";
        }
        String strSql = "select v from Stock v";

        List<Stock> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public List<Stock> searchB(String saleInvId) {
        String strFilter = "";
        if (!saleInvId.equals("-")) {
            strFilter = "v.brand = '" + saleInvId + "'";
        }
        String strSql = "select v from Stock v";

        List<Stock> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public List<Stock> searchM(String updatedDate) {
        String strSql = "select o from Stock o where o.updatedDate > '" + updatedDate + "'";
        return (List<Stock>) findHSQL(strSql);

    }
}
