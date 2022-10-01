/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Stock;
import cv.api.inv.entity.StockKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockDaoImpl extends AbstractDao<StockKey, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        persist(stock);
        return stock;
    }

    @Override
    public Stock findById(StockKey key) {
        return getByKey(key);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        String hsql = "select o from Stock o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String sql = "delete from stock where stock_code = '" + id + "'";
        execSQL(sql);
        return 1;
    }

    @Override
    public List<Stock> findActiveStock(String compCode) {
        String hsql = "select o from Stock o where o.active is true and o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);

    }

    @Override
    public List<Stock> search(String stockCode, String stockType, String cat, String brand) {
        String hsql = "select o from Stock o where o.active = 1";
        if (!stockCode.equals("-")) {
            hsql += " and o.key.stockCode ='" + stockCode + "'\n";
        }
        if (!stockType.equals("-")) {
            hsql += " and o.stockType.stockTypeCode ='" + stockType + "'\n";
        }
        if (!cat.equals("-")) {
            hsql += " and o.category.catCode ='" + cat + "'\n";
        }
        if (!brand.equals("-")) {
            hsql += " and o.brand.brandCode ='" + brand + "'\n";
        }
        return findHSQL(hsql);
    }

    @Override
    public List<Stock> getStock(String str, String compCode) {
        String hsql = "select o from Stock o where o.key.compCode='" + compCode + "' and o.active =1\n";
        String filter = "and o.userCode like '" + str + "%'";
        int limit = 8;
        List<Stock> list = findHSQL(hsql + filter, limit);
        if (list.isEmpty()) {
            filter = "and o.stockName like '" + str + "%'";
            list = findHSQL(hsql + filter, limit);
            if (list.isEmpty()) {
                filter = "and o.stockName like '%" + str + "%'";
                list = findHSQL(hsql + filter, limit);
            }
        }
        return list;
    }
}
