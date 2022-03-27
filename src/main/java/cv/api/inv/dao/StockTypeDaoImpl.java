/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockTypeDaoImpl extends AbstractDao<String, StockType> implements StockTypeDao {

    @Override
    public StockType save(StockType item) {
        persist(item);
        return item;
    }

    @Override
    public List<StockType> findAll(String compCode) {
        String hsql = "select o from StockType o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockType o where o.stockTypeCode ='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public StockType findByCode(String code) {
        return getByKey(code);
    }

}
