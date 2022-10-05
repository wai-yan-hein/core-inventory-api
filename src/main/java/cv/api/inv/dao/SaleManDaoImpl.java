/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.SaleMan;
import cv.api.inv.entity.SaleManKey;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
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

}
