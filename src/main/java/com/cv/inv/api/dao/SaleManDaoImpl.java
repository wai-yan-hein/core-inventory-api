/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleMan;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleManDaoImpl extends AbstractDao<String, SaleMan> implements SaleManDao {

    @Override
    public SaleMan save(SaleMan saleMan) {
        persist(saleMan);
        return saleMan;
    }

    @Override
    public List<SaleMan> findAll(String compCode) {
        String hsql = "select o from SaleMan o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from SaleMan o where o.saleManCode ='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public SaleMan findByCode(String id) {
        return getByKey(id);
    }

}
