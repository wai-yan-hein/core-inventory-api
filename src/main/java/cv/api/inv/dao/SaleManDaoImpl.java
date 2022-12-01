/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.SaleMan;
import cv.api.inv.entity.SaleManKey;
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
public class SaleManDaoImpl extends AbstractDao<SaleManKey, SaleMan> implements SaleManDao {

    @Override
    public SaleMan save(SaleMan saleMan) {
        persist(saleMan);
        return saleMan;
    }

    @Override
    public List<SaleMan> findAll(String compCode, Integer deptId) {
        String hsql = "select o from SaleMan o where o.key.compCode = '" + compCode + "' and o.key.deptId =" + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from SaleMan o where o.saleManCode ='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public SaleMan findByCode(SaleManKey id) {
        return getByKey(id);
    }

    @Override
    public List<SaleMan> unUpload() {
        String hsql = "select o from SaleMan o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public List<SaleMan> getSaleMan(String updatedDate) {
        String hsql = "select o from SaleMan o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from sale_man";
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

}
