/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.StockBrand;
import cv.api.inv.entity.StockBrandKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockBrandDaoImpl extends AbstractDao<StockBrandKey, StockBrand> implements StockBrandDao {

    @Override
    public StockBrand save(StockBrand item)  {
        persist(item);
        return item;
    }

    @Override
    public List<StockBrand> findAll(String compCode, Integer deptId) {
        String hsql = "select o from StockBrand o where o.key.compCode = '" + compCode + "' and (o.key.deptId=" + deptId + " or 0 =" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from StockBrand o where o.brandCode='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public List<StockBrand> unUpload() {
        String hsql = "select o from StockBrand o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_brand";
        ResultSet rs = getResultSet(sql);
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
        return Util1.getOldDate();
    }

    @Override
    public List<StockBrand> getBrand(String updatedDate) {
        String hsql = "select o from StockBrand o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public StockBrand findByCode(StockBrandKey code) {
        return getByKey(code);
    }

}
