/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockUnit;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockUnitDaoImpl extends AbstractDao<String, StockUnit> implements StockUnitDao {

    @Override
    public StockUnit save(StockUnit item) {
        persist(item);
        return item;
    }

    @Override
    public List<StockUnit> findAll(String compCode,Integer deptId) {
        String hsql = "select o from StockUnit o where o.key.compCode = '" + compCode + "' and o.key.deptId ="+deptId+"";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockUnit o where o.itemUnitCode='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public StockUnit findByCode(String code) {
        return getByKey(code);
    }

}
