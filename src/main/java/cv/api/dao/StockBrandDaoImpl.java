/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockBrand;
import cv.api.entity.StockBrandKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockBrandDaoImpl extends AbstractDao<StockBrandKey, StockBrand> implements StockBrandDao {

    @Override
    public StockBrand save(StockBrand item) {
        item.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(item, item.getKey());
        return item;
    }

    @Override
    public List<StockBrand> findAll(String compCode, Integer deptId) {
        String hsql = "select o from StockBrand o where o.key.compCode = '" + compCode + "' and (o.deptId=" + deptId + " or 0 =" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        return 1;
    }

    @Override
    public List<StockBrand> unUpload() {
        String hsql = "select o from StockBrand o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }



    @Override
    public List<StockBrand> getBrand(LocalDateTime updatedDate) {
        String hsql = "select o from StockBrand o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public StockBrand findByCode(StockBrandKey code) {
        return getByKey(code);
    }

}
