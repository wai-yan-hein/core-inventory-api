/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.SaleMan;
import cv.api.entity.SaleManKey;
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
public class SaleManDaoImpl extends AbstractDao<SaleManKey, SaleMan> implements SaleManDao {

    @Override
    public SaleMan save(SaleMan saleMan) {
        saveOrUpdate(saleMan, saleMan.getKey());
        return saleMan;
    }

    @Override
    public List<SaleMan> findAll(String compCode, Integer deptId) {
        String hsql = "select o from SaleMan o where o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0 = " + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        return 1;
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
    public List<SaleMan> getSaleMan(LocalDateTime updatedDate) {
        String hsql = "select o from SaleMan o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from sale_man";
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
        return Util1.getOldDate();
    }

}
