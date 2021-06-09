/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockBrand;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class StockBrandDaoImpl extends AbstractDao<String, StockBrand> implements StockBrandDao {

    @Override
    public StockBrand save(StockBrand item) {
        persist(item);
        return item;
    }

    @Override
    public List<StockBrand> findAll() {
        String hsql = "select o from StockBrand o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockBrand o where o.brandId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

}
