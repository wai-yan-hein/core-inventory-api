/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockBrand;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockBrandDaoImpl extends AbstractDao<String, StockBrand> implements StockBrandDao {

    @Override
    public StockBrand save(StockBrand item) throws Exception {
        persist(item);
        return item;
    }

    @Override
    public List<StockBrand> findAll(String compCode, Integer deptId) {
        String hsql = "select o from StockBrand o where o.key.compCode = '" + compCode + "' and o.key.deptId=" + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockBrand o where o.brandCode='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public StockBrand findByCode(String code) {
        return getByKey(code);
    }

}
